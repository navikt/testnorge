import { useEffect, useRef } from 'react'
import useSWR, { useSWRConfig, type KeyedMutator } from 'swr'
import api from '@/api'
import { backgroundFetcher } from '@/api'
import { BACKGROUND_REQUEST_HEADERS } from '@/components/utlogging/sessionExpired'
import { NotFoundError } from '@/error'

const dollyStatusUrl = '/testnav-dollystatus/api/v1/statusoversikt'
const testkjoringerUrl = '/testnav-dollystatus/api/v1/testkjoringer'
const feiledeTestkjoringerUrl = `${testkjoringerUrl}/feilede`
const maxTimerDelay = 2_147_483_647
const retryDelay = 60_000
const maxAutomaticAttempts = 3

export type DollyStatusNivaa = 'OK' | 'ADVARSEL' | 'FEIL' | 'UKJENT'
export type DollyStatusKjoringResult = 'startet' | 'pagar' | 'ingenProblemer' | 'nedkjoling'

export interface DollyStatusProblem {
	systemId: string
	navn: string
	miljo: string | null
	tilstand: string
	melding: string | null
	tidspunkt: string | null
}

export interface DollyStatusOversikt {
	status: DollyStatusNivaa
	kjoringPagaar: boolean
	sistOppdatert: string | null
	nesteAutomatiskeKjoring: string | null
	nesteManuelleKjoringTidligst: string | null
	problemer: DollyStatusProblem[]
}

const isRecord = (value: unknown): value is Record<string, unknown> =>
	typeof value === 'object' && value !== null && !Array.isArray(value)

const isOptionalTimestamp = (value: unknown): value is string | null | undefined =>
	value === null ||
	value === undefined ||
	(typeof value === 'string' && !Number.isNaN(Date.parse(value)))

const isDollyStatusNivaa = (value: unknown): value is DollyStatusNivaa =>
	value === 'OK' || value === 'ADVARSEL' || value === 'FEIL' || value === 'UKJENT'

const normalizeDollyStatusProblem = (value: unknown): DollyStatusProblem | undefined => {
	if (!isRecord(value)) {
		return undefined
	}
	if (
		typeof value.systemId === 'string' &&
		typeof value.navn === 'string' &&
		(value.miljo === null || value.miljo === undefined || typeof value.miljo === 'string') &&
		typeof value.tilstand === 'string' &&
		(value.melding === null || value.melding === undefined || typeof value.melding === 'string') &&
		isOptionalTimestamp(value.tidspunkt)
	) {
		return {
			systemId: value.systemId,
			navn: value.navn,
			miljo: value.miljo ?? null,
			tilstand: value.tilstand,
			melding: value.melding ?? null,
			tidspunkt: value.tidspunkt ?? null,
		}
	}
	return undefined
}

const validateDollyStatus = (payload: unknown): DollyStatusOversikt => {
	if (
		!isRecord(payload) ||
		!isDollyStatusNivaa(payload.status) ||
		typeof payload.kjoringPagaar !== 'boolean' ||
		!Array.isArray(payload.problemer) ||
		!isOptionalTimestamp(payload.sistOppdatert) ||
		!isOptionalTimestamp(payload.nesteAutomatiskeKjoring) ||
		!isOptionalTimestamp(payload.nesteManuelleKjoringTidligst)
	) {
		throw new Error('Ugyldig statusoversikt fra dollystatus.')
	}

	const problemer = payload.problemer.map(normalizeDollyStatusProblem)
	if (problemer.some((problem) => problem === undefined)) {
		throw new Error('Ugyldig statusoversikt fra dollystatus.')
	}

	return {
		status: payload.status,
		kjoringPagaar: payload.kjoringPagaar,
		sistOppdatert: payload.sistOppdatert ?? null,
		nesteAutomatiskeKjoring: payload.nesteAutomatiskeKjoring ?? null,
		nesteManuelleKjoringTidligst: payload.nesteManuelleKjoringTidligst ?? null,
		problemer: problemer.filter((problem): problem is DollyStatusProblem => problem !== undefined),
	}
}

const dollyStatusFetcher = async (url: string) => validateDollyStatus(await backgroundFetcher(url))

const getErrorStatus = (error: unknown): number | undefined =>
	isRecord(error) && typeof error.status === 'number' ? error.status : undefined

const postTestkjoring = (url: string) =>
	api.fetch(url, {
		method: 'POST',
		headers: BACKGROUND_REQUEST_HEADERS,
		retry: false,
	})

const postManuellTestkjoring = async (url: string): Promise<DollyStatusKjoringResult> => {
	try {
		const response = await postTestkjoring(url)
		if (response.status === 202) {
			return 'startet'
		}
		if (response.status === 409) {
			return 'pagar'
		}
		if (response.status === 404) {
			return 'ingenProblemer'
		}
		if (response.status === 429) {
			return 'nedkjoling'
		}
		throw Object.assign(new Error('Uventet svar ved start av testkjøring.'), {
			status: response.status,
		})
	} catch (error) {
		if (error instanceof NotFoundError) {
			return 'ingenProblemer'
		}
		const status = getErrorStatus(error)
		if (status === 409) {
			return 'pagar'
		}
		if (status === 429) {
			return 'nedkjoling'
		}
		throw error
	}
}

export interface UseDollyStatusResult {
	statusoversikt: DollyStatusOversikt | undefined
	loading: boolean
	error: Error | undefined
	kjorFeilede: () => Promise<DollyStatusKjoringResult>
	kjorUtlopte: () => Promise<DollyStatusKjoringResult>
	mutate: KeyedMutator<DollyStatusOversikt>
}

export const useDollyStatus = (): UseDollyStatusResult => {
	const { data, isLoading, error, mutate } = useSWR<DollyStatusOversikt, Error>(
		dollyStatusUrl,
		dollyStatusFetcher,
		{
			refreshInterval: (latestData) => (latestData?.kjoringPagaar ? 15000 : 60000),
		},
	)

	const startManualKjoring = async (url: string): Promise<DollyStatusKjoringResult> => {
		try {
			return await postManuellTestkjoring(url)
		} finally {
			await mutate()
		}
	}

	return {
		statusoversikt: data,
		loading: isLoading,
		error,
		kjorFeilede: () => startManualKjoring(feiledeTestkjoringerUrl),
		kjorUtlopte: () => startManualKjoring(testkjoringerUrl),
		mutate,
	}
}

export const useAutomatiskDollyStatusKjoring = (
	statusoversikt: DollyStatusOversikt | undefined,
	enabled: boolean,
) => {
	const attemptRef = useRef<{ deadline: string | undefined; attempts: number }>({
		deadline: undefined,
		attempts: 0,
	})
	const { mutate: mutateStatus } = useSWRConfig()
	const deadline = statusoversikt?.nesteAutomatiskeKjoring ?? undefined
	const kjoringPagaar = statusoversikt?.kjoringPagaar

	useEffect(() => {
		if (attemptRef.current.deadline !== deadline) {
			attemptRef.current = { deadline, attempts: 0 }
		}
		if (!enabled || !deadline || kjoringPagaar) {
			return
		}

		const deadlineTime = Date.parse(deadline)
		if (Number.isNaN(deadlineTime)) {
			return
		}

		const currentAttempt = attemptRef.current
		if (currentAttempt.attempts >= maxAutomaticAttempts) {
			return
		}

		let active = true
		let timer: number | undefined
		let visibilityListener: (() => void) | undefined
		const targetTime = Math.max(deadlineTime, Date.now()) + Math.random() * 30000

		const removeVisibilityListener = () => {
			if (visibilityListener) {
				document.removeEventListener('visibilitychange', visibilityListener)
				visibilityListener = undefined
			}
		}

		const schedule = (time: number, callback: () => void) => {
			timer = window.setTimeout(
				() => {
					if (!active) {
						return
					}
					const remaining = time - Date.now()
					if (remaining > 0) {
						schedule(time, callback)
						return
					}
					callback()
				},
				Math.min(Math.max(0, time - Date.now()), maxTimerDelay),
			)
		}

		const runWhenVisible = (callback: () => void) => {
			if (!active) {
				return
			}
			if (document.visibilityState !== 'visible') {
				visibilityListener = () => {
					if (document.visibilityState === 'visible') {
						removeVisibilityListener()
						callback()
					}
				}
				document.addEventListener('visibilitychange', visibilityListener)
				return
			}
			callback()
		}

		const startTestkjoring = () => {
			runWhenVisible(() => {
				if (!active || attemptRef.current !== currentAttempt) {
					return
				}
				currentAttempt.attempts += 1
				void (async () => {
					let postError: unknown
					try {
						const response = await postTestkjoring(testkjoringerUrl)
						if (response.status !== 202 && response.status !== 409 && response.status !== 429) {
							if (response.status === 404) {
								postError = new NotFoundError()
							} else {
								postError = Object.assign(new Error('Uventet svar ved start av testkjøring.'), {
									status: response.status,
								})
							}
						}
					} catch (error) {
						postError = error
					}

					if (!active || attemptRef.current !== currentAttempt) {
						return
					}
					try {
						await mutateStatus(dollyStatusUrl)
					} catch (error) {
						console.error('Klarte ikke å hente oppdatert teststatus.', error)
					}
					if (!active || attemptRef.current !== currentAttempt || postError === undefined) {
						return
					}
					if (getErrorStatus(postError) === 409 || getErrorStatus(postError) === 429) {
						return
					}
					if (currentAttempt.attempts >= maxAutomaticAttempts) {
						console.error('Klarte ikke å starte automatisk testkjøring.', postError)
						return
					}
					schedule(Date.now() + retryDelay, () => startTestkjoring())
				})()
			})
		}

		schedule(targetTime, startTestkjoring)

		return () => {
			active = false
			if (timer !== undefined) {
				window.clearTimeout(timer)
			}
			removeVisibilityListener()
		}
	}, [deadline, enabled, kjoringPagaar, mutateStatus])
}

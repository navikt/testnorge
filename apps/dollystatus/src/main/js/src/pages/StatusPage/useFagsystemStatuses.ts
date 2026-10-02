import { useCallback, useEffect, useMemo, useState } from 'react'

import { ApiError } from '@/services/api'
import statusApi, { FagsystemStatus } from '@/services/statusApi'

const POLLING_INTERVAL_MILLISECONDS = 2_000

const TECHNICAL_SYSTEM_IDS = new Set([
	'aareg',
	'arbeidsplassen-cv',
	'dokarkiv',
	'fullmakt',
	'inntektsmelding',
	'kdi',
	'kelvin-aap',
	'medl',
	'oppfoelgingsvedtak-14a',
	'organisasjon-forvalter',
	'pensjon-oevrige',
	'sigrun',
	'sykemelding',
	'tags',
	'yrkesskade',
])

const isTechnical = (statuses: FagsystemStatus[]) =>
	TECHNICAL_SYSTEM_IDS.has(statuses[0].systemId) ||
	statuses.every((status) => status.state === 'TECHNICAL_ONLY')

const sortPriority = (statuses: FagsystemStatus[]) =>
	statuses.some((status) => status.state === 'BLOCKED') ? 1 : 0

const compareGroups = (left: FagsystemStatus[], right: FagsystemStatus[]) =>
	sortPriority(left) - sortPriority(right) ||
	left[0].displayName.localeCompare(right[0].displayName, 'nb')

export const useFagsystemStatuses = () => {
	const [statuses, setStatuses] = useState<FagsystemStatus[]>([])
	const [initialLoading, setInitialLoading] = useState(true)
	const [activeRunId, setActiveRunId] = useState<string | null>(null)
	const [startingSystemId, setStartingSystemId] = useState<string | null>(null)
	const [errorMessage, setErrorMessage] = useState<string | null>(null)

	const refreshStatuses = useCallback(async () => {
		const updatedStatuses = await statusApi.getStatuses()
		setStatuses(updatedStatuses)
		if (!updatedStatuses.some((status) => isRunning(status.state))) {
			setActiveRunId(null)
		}
		return updatedStatuses
	}, [])

	useEffect(() => {
		let active = true

		const start = async () => {
			try {
				const initialStatuses = await statusApi.getStatuses()
				if (!active) {
					return
				}
				setStatuses(initialStatuses)
				const run = await statusApi.startExpiredTests()
				if (!active) {
					return
				}
				setActiveRunId(run.runId)
				await refreshStatuses()
			} catch (error) {
				if (error instanceof ApiError && error.response.status === 404) {
					return
				}
				if (active) {
					setErrorMessage('Vi kunne ikke starte statussjekken. Last inn siden på nytt.')
				}
			} finally {
				if (active) {
					setInitialLoading(false)
				}
			}
		}

		void start()
		return () => {
			active = false
		}
	}, [refreshStatuses])

	useEffect(() => {
		if (activeRunId === null) {
			return
		}
		const intervalId = window.setInterval(() => {
			refreshStatuses().catch(() => {
				setErrorMessage('Vi kunne ikke oppdatere statusene. Prøv å laste inn siden på nytt.')
			})
		}, POLLING_INTERVAL_MILLISECONDS)
		return () => window.clearInterval(intervalId)
	}, [activeRunId, refreshStatuses])

	const rerun = useCallback(
		async (systemId: string) => {
			setStartingSystemId(systemId)
			setErrorMessage(null)
			try {
				const run = await statusApi.startSystemTest(systemId)
				setActiveRunId(run.runId)
				await refreshStatuses()
			} catch (error) {
				const problem = await statusApi.readProblem(error)
				if (error instanceof ApiError && error.response.status === 409) {
					const updatedStatuses = await refreshStatuses()
					const runningStatus = updatedStatuses.find((status) => isRunning(status.state))
					setActiveRunId(runningStatus?.runId ?? null)
				}
				setErrorMessage(problem?.message ?? 'Vi kunne ikke starte testen. Prøv igjen senere.')
			} finally {
				setStartingSystemId(null)
			}
		},
		[refreshStatuses],
	)

	const groupedStatuses = useMemo(
		() =>
			Object.values(
				statuses.reduce<Record<string, FagsystemStatus[]>>((groups, status) => {
					groups[status.systemId] = [...(groups[status.systemId] ?? []), status]
					return groups
				}, {}),
			),
		[statuses],
	)
	const functionalStatuses = useMemo(
		() => groupedStatuses.filter((group) => !isTechnical(group)).sort(compareGroups),
		[groupedStatuses],
	)
	const technicalStatuses = useMemo(
		() => groupedStatuses.filter(isTechnical).sort(compareGroups),
		[groupedStatuses],
	)

	return {
		activeRunId,
		errorMessage,
		functionalStatuses,
		groupedStatuses,
		initialLoading,
		rerun,
		startingSystemId,
		technicalStatuses,
	}
}

export const isRunning = (state: FagsystemStatus['state']) =>
	['RUNNING', 'PREFLIGHT', 'CREATE', 'VERIFY', 'CLEANUP'].includes(state)

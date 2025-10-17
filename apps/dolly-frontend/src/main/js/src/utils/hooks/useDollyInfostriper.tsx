import { useCallback } from 'react'
import useSWR, { mutate as globalMutate } from 'swr'

export interface InfoStripeType {
	id: number
	message: string
	type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS'
	start: Date
	expires: Date
}

export interface CreateInfostripeInput {
	type: InfoStripeType['type']
	message: string
	start: Date | string
	expires: Date | string
}

export interface UpdateInfostripeInput extends CreateInfostripeInput {
	id: number
}

const INFOSTRIPE_URL = '/dolly-backend/api/v1/infostripe'
const FUTURE_KEY = `${INFOSTRIPE_URL}?inkluderFremtidige=true`

export const useDollyInfostriper = (inkluderFremtidige = false) => {
	const listUrl = inkluderFremtidige ? FUTURE_KEY : INFOSTRIPE_URL

	const { data, isLoading, error, mutate } = useSWR<InfoStripeType[], Error>(
		listUrl,
		async (url: string) => {
			const resp = await fetch(url)
			if (!resp.ok) throw new Error(`Failed ${resp.status}`)
			return resp.json()
		},
		{ refreshInterval: 60000, dedupingInterval: 60000 },
	)

	const revalidateAll = useCallback(async () => {
		await mutate()
		await globalMutate(FUTURE_KEY)
	}, [mutate])

	const createInfostripe = useCallback(
		async (input: CreateInfostripeInput) => {
			const resp = await fetch(INFOSTRIPE_URL, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(input),
			})
			if (!resp.ok) throw new Error(`Create failed ${resp.status}`)
			await revalidateAll()
		},
		[revalidateAll],
	)

	const updateInfostripe = useCallback(
		async (input: UpdateInfostripeInput) => {
			const url = `${INFOSTRIPE_URL}/${input.id}`
			const resp = await fetch(url, {
				method: 'PUT',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(input),
			})
			if (!resp.ok) throw new Error(`Update failed ${resp.status}`)
			await revalidateAll()
		},
		[revalidateAll],
	)

	const deleteInfostripe = useCallback(
		async (id: number) => {
			const url = `${INFOSTRIPE_URL}/${id}`
			const previous = data
			await mutate((curr) => (curr ? curr.filter((c) => c.id !== id) : curr), { revalidate: false })
			const resp = await fetch(url, { method: 'DELETE' })
			if (!resp.ok) {
				await mutate(previous, { revalidate: false })
				throw new Error(`Delete failed ${resp.status}`)
			}
			await revalidateAll()
		},
		[data, mutate, revalidateAll],
	)

	return {
		infostriper: Array.isArray(data) ? data : [],
		loading: isLoading,
		error,
		createInfostripe,
		updateInfostripe,
		deleteInfostripe,
	}
}

import { useCallback } from 'react'
import useSWR from 'swr'
import { fetcher } from '@/api'

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

const normalize = (raw: any): InfoStripeType => ({
	id: raw.id,
	message: raw.message,
	type: raw.type.toLowerCase(),
	start: new Date(raw.start),
	expires: new Date(raw.expires),
})

export const useDollyInfostriper = () => {
	const { data, isLoading, error, mutate } = useSWR<InfoStripeType[], Error>(
		INFOSTRIPE_URL,
		fetcher,
		{ refreshInterval: 60000, dedupingInterval: 60000 },
	)

	const createInfostripe = useCallback(
		async (input: CreateInfostripeInput) => {
			const resp = await fetch(INFOSTRIPE_URL, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify(input),
			})
			if (!resp.ok) throw new Error(`Create failed ${resp.status}`)
			const created = normalize(await resp.json())
			await mutate((curr) => (curr ? [...curr, created] : [created]), { revalidate: false })
			return created
		},
		[mutate],
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
			const updated = normalize(await resp.json())
			await mutate((curr) =>
				curr ? curr.map((c) => (c.id === updated.id ? updated : c)) : [updated],
				{ revalidate: false }
			)
			return updated
		},
		[mutate],
	)

	const deleteInfostripe = useCallback(
		async (id: number) => {
			const url = `${INFOSTRIPE_URL}/${id}`
			const prev = data
			await mutate((curr) => (curr ? curr.filter((c) => c.id !== id) : curr), { revalidate: false })
			const resp = await fetch(url, { method: 'DELETE' })
			if (!resp.ok) {
				await mutate(prev, { revalidate: false })
				throw new Error(`Delete failed ${resp.status}`)
			}
		},
		[data, mutate],
	)

	return {
		infostriper: data && Array.isArray(data) ? data : [],
		loading: isLoading,
		error,
		createInfostripe,
		updateInfostripe,
		deleteInfostripe,
	}
}

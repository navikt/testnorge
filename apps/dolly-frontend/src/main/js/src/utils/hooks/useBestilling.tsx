import useSWR from 'swr'
import { fetcher } from '~/api'

const getBestillingerGruppe = (gruppeId: number) =>
	`/dolly-backend/api/v1/bestilling/gruppe/${gruppeId}`

type Bestillingsstatus = {
	id: number
	environments: string[]
	antallIdenter: number
	antallLevert: number
	bestilling: any
	bruker: any
	gruppeId: number
}

export const useBestillingerGruppe = (gruppeId: number) => {
	const { data, error } = useSWR<Bestillingsstatus[], Error>(
		getBestillingerGruppe(gruppeId),
		fetcher
	)

	const bestillingerSorted = data
		?.sort((bestilling, bestilling2) => (bestilling.id < bestilling2.id ? 1 : -1))
		.reduce((acc: { [key: string]: Bestillingsstatus }, curr) => ((acc[curr.id] = curr), acc), {})

	return {
		bestillinger: data,
		bestillingerById: bestillingerSorted,
		loading: !error && !data,
		error: error,
	}
}

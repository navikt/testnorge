import useSWR from 'swr'
import { fetcher, multiFetcherAll, multiFetcherFagsystemer } from '~/api'
import { useTpEnvironments } from '~/utils/hooks/useEnvironments'

const tpUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/dolly-backend/api/v1/tp/forhold/${ident}/${miljo}`,
		miljo: miljo,
	}))

export const useTpData = (ident) => {
	const { tpEnvironments } = useTpEnvironments()

	const { data, error } = useSWR<any, Error>(
		[tpUrl(ident, tpEnvironments)],
		multiFetcherFagsystemer
	)

	// Lag noe sånt:
	// if (!harTpBestilling) {
	// 	return {
	// 		loading: false,
	// 	}
	// }

	return {
		// tpData: data,
		tpData: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: !error && !data,
		error: error,
	}
}

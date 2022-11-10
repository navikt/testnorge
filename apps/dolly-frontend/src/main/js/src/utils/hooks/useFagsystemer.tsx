import useSWR from 'swr'
import { multiFetcherFagsystemer } from '~/api'
import { useTpEnvironments } from '~/utils/hooks/useEnvironments'

const tpUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/dolly-backend/api/v1/tp/forhold/${ident}/${miljo}`,
		miljo: miljo,
	}))

export const useTpData = (ident, harTpBestilling) => {
	const { tpEnvironments } = useTpEnvironments()

	if (!harTpBestilling) {
		return {
			loading: false,
		}
	}

	const { data, error } = useSWR<any, Error>(
		[tpUrl(ident, tpEnvironments)],
		multiFetcherFagsystemer
	)

	return {
		tpData: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: !error && !data,
		error: error,
	}
}

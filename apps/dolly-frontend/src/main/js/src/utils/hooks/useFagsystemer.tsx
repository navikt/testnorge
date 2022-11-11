import useSWR from 'swr'
import { multiFetcherFagsystemer } from '~/api'
import { usePensjonEnvironments } from '~/utils/hooks/useEnvironments'

const tpUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/testnav-pensjon-testdata-facade-proxy/api/v1/tp/forhold?fnr=${ident}&miljo=${miljo}`,
		miljo: miljo,
	}))

export const useTpData = (ident, harTpBestilling) => {
	const { pensjonEnvironments } = usePensjonEnvironments()

	if (!harTpBestilling) {
		return {
			loading: false,
		}
	}

	const { data, error } = useSWR<any, Error>(
		[
			tpUrl(ident, pensjonEnvironments),
			{ 'Nav-Call-Id': 'Dolly', 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly' },
		],
		multiFetcherFagsystemer
	)

	return {
		tpData: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: !error && !data,
		error: error,
	}
}

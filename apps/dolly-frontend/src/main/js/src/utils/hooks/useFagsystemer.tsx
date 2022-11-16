import useSWR from 'swr'
import { multiFetcherFagsystemer } from '~/api'
import { useInstEnvironments, usePensjonEnvironments } from '~/utils/hooks/useEnvironments'

const poppUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/testnav-pensjon-testdata-facade-proxy/api/v1/inntekt?fnr=${ident}&miljo=${miljo}`,
		miljo: miljo,
	}))

const tpUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/testnav-pensjon-testdata-facade-proxy/api/v1/tp/forhold?fnr=${ident}&miljo=${miljo}`,
		miljo: miljo,
	}))

export const usePoppData = (ident, harPoppBestilling) => {
	const { pensjonEnvironments } = usePensjonEnvironments()

	if (!harPoppBestilling) {
		return {
			loading: false,
		}
	}

	const { data, error } = useSWR<any, Error>(
		[
			poppUrl(ident, pensjonEnvironments),
			{ 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly' },
			'inntekter',
		],
		multiFetcherFagsystemer
	)

	return {
		poppData: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: !error && !data,
		error: error,
	}
}

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
			{ 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly' },
		],
		multiFetcherFagsystemer
	)

	return {
		tpData: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: !error && !data,
		error: error,
	}
}

export const useInstData = (ident, harTpBestilling) => {
	const { instEnvironments } = useInstEnvironments()

	if (!harTpBestilling) {
		return {
			loading: false,
		}
	}

	const { data, error } = useSWR<any, Error>(
		[
			tpUrl(ident, instEnvironments),
			{ 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly' },
		],
		multiFetcherFagsystemer
	)

	return {
		tpData: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: !error && !data,
		error: error,
	}
}

import useSWR from 'swr'
import { multiFetcherInst, postFetcher } from '@/api'
import { useInstEnvironments } from '@/utils/hooks/useEnvironments'

const instRoot = '/testnav-dolly-proxy/inst/api'

const instUrl = (ident: string, miljoer: Array<string>) =>
	miljoer?.map((miljo) => ({
		url: `${instRoot}/v1/institusjonsopphold/person?environments=${miljo}`,
		miljo: miljo,
	}))

const kdiUrl = `${instRoot}/v2/kdi/person/soek`

const fengselUrl = `${instRoot}/v1/institusjon/fengsel`

export const useInstData = (ident: string, harInstBestilling: boolean) => {
	const { instEnvironments } = useInstEnvironments()

	const { data, isLoading, error } = useSWR<any, Error>(
		[harInstBestilling ? instUrl(ident, instEnvironments) : null, { norskident: ident }],
		([url, headers]) => multiFetcherInst(url, headers),
	)

	return {
		instData: data?.sort?.((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: isLoading,
		error: error,
	}
}

export const useKdiData = (ident: string, harKdiBestilling: boolean) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		ident && harKdiBestilling ? [kdiUrl, ident] : null,
		([url, norskident]: [string, string]) =>
			postFetcher(url, { environment: 'q2', norskident: norskident }),
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	return {
		kdiData: data?.data,
		loading: isLoading,
		error: error,
	}
}

export const useFengsel = () => {
	const { data, isLoading, error } = useSWR<any, Error>(fengselUrl, (url: string) =>
		postFetcher(url, {}),
	)

	return {
		fengsler: data,
		loading: isLoading,
		error: error,
	}
}

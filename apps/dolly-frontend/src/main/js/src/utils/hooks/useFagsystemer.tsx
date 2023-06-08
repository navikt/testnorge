import useSWR from 'swr'
import { fetcher, multiFetcherDokarkiv, multiFetcherInst, multiFetcherPensjon } from '@/api'
import {
	useDokarkivEnvironments,
	useInstEnvironments,
	usePensjonEnvironments,
} from '@/utils/hooks/useEnvironments'

import { useTransaksjonsid } from '@/utils/hooks/useTransaksjonsid'

const poppUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/testnav-pensjon-testdata-facade-proxy/api/v1/inntekt?fnr=${ident}`,
		miljo: miljo,
	}))

const tpUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/testnav-pensjon-testdata-facade-proxy/api/v1/tp/forhold?fnr=${ident}&miljo=${miljo}`,
		miljo: miljo,
	}))

const instUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/testnav-inst-proxy/api/v1/institusjonsopphold/person?environments=${miljo}`,
		miljo: miljo,
	}))

const journalpostUrl = (transaksjonsid, miljoer) =>
	miljoer?.map((miljoe) => {
		const journalpostId = transaksjonsid?.find((id) => id.miljoe === miljoe)?.transaksjonId
			?.journalpostId
		return {
			url: journalpostId
				? `/testnav-joark-dokument-service/api/v2/journalpost/${journalpostId}`
				: null,
			miljo: miljoe,
		}
	})

const histarkUrl = (transaksjonsid: string) =>
	transaksjonsid ? `/testnav-histark-proxy/api/saksmapper/${transaksjonsid}` : null

const arbeidsforholdcvUrl = '/testnav-arbeidsplassencv-proxy/rest/v2/cv'
const arbeidsforholdcvHjemmelUrl = '/testnav-arbeidsplassencv-proxy/rest/hjemmel'

export const usePoppData = (ident, harPoppBestilling) => {
	const { pensjonEnvironments } = usePensjonEnvironments()

	const { data, error } = useSWR<any, Error>(
		[
			harPoppBestilling ? poppUrl(ident, pensjonEnvironments) : null,
			{ 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly' },
		],
		([url, headers]) => multiFetcherPensjon(url, headers)
	)

	return {
		poppData: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: !error && !data,
		error: error,
	}
}

export const useTpData = (ident, harTpBestilling) => {
	const { pensjonEnvironments } = usePensjonEnvironments()

	const { data, error } = useSWR<any, Error>(
		[
			harTpBestilling ? tpUrl(ident, pensjonEnvironments) : null,
			{ 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly' },
		],
		([url, headers]) => multiFetcherPensjon(url, headers)
	)

	return {
		tpData: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: !error && !data,
		error: error,
	}
}

export const useInstData = (ident, harInstBestilling) => {
	const { instEnvironments } = useInstEnvironments()

	const { data, error } = useSWR<any, Error>(
		[harInstBestilling ? instUrl(ident, instEnvironments) : null, { norskident: ident }],
		([url, headers]) => multiFetcherInst(url, headers)
	)

	return {
		instData: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: !error && !data,
		error: error,
	}
}

export const useDokarkivData = (ident, harDokarkivbestilling) => {
	const { transaksjonsid } = useTransaksjonsid('DOKARKIV', ident)
	const { dokarkivEnvironments } = useDokarkivEnvironments()

	const { data, error } = useSWR<any, Error>(
		harDokarkivbestilling ? journalpostUrl(transaksjonsid, dokarkivEnvironments) : null,
		multiFetcherDokarkiv
	)

	return {
		dokarkivData: data?.filter((journalpost) => journalpost.data?.journalpostId !== null),
		loading: !error && !data,
		error: error,
	}
}

export const useHistarkData = (ident, harHistarkbestilling) => {
	const { transaksjonsid } = useTransaksjonsid('HISTARK', ident)

	const histarkId = transaksjonsid?.[0]?.transaksjonId?.dokumentInfoId

	const { data, isLoading, error } = useSWR<any, Error>(
		harHistarkbestilling ? histarkUrl(histarkId) : null,
		fetcher
	)

	return {
		histarkData: data,
		loading: isLoading,
		error: error,
	}
}

export const useArbeidsplassencvData = (ident: string, harArbeidsplassenBestilling: boolean) => {
	const { data, error } = useSWR<any, Error>(
		[harArbeidsplassenBestilling ? arbeidsforholdcvUrl : null, { fnr: ident }],
		([url, headers]) => fetcher(url, headers)
	)

	return {
		arbeidsplassencvData: data,
		loading: !error && !data,
		error: error,
	}
}

export const useArbeidsplassencvHjemmel = (ident: string) => {
	const { data, error } = useSWR<any, Error>(
		[arbeidsforholdcvHjemmelUrl, { fnr: ident }],
		([url, headers]) => fetcher(url, headers)
	)

	return {
		arbeidsplassencvHjemmel: data,
		loading: !error && !data,
		error: error,
	}
}

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

const arbeidsforholdcvUrl = '/testnav-arbeidsplassencv-proxy/rest/v2/cv'
const arbeidsforholdcvHjemmelUrl = '/testnav-arbeidsplassencv-proxy/rest/hjemmel'

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

	if (!harInstBestilling) {
		return {
			loading: false,
		}
	}

	const { data, error } = useSWR<any, Error>(
		[instUrl(ident, instEnvironments), { norskident: ident }],
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

	if (!harDokarkivbestilling) {
		return {
			loading: false,
		}
	}

	const { data, error } = useSWR<any, Error>(
		journalpostUrl(transaksjonsid, dokarkivEnvironments),
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

	if (!harHistarkbestilling) {
		return {
			loading: false,
		}
	}

	return {
		histarkData: transaksjonsid,
		loading: false,
		error: null,
	}
}

export const useArbeidsplassencvData = (ident: string, harArbeidsplassenBestilling: boolean) => {
	if (!harArbeidsplassenBestilling) {
		return {
			loading: false,
		}
	}

	const { data, error } = useSWR<any, Error>(
		[arbeidsforholdcvUrl, { fnr: ident }],
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

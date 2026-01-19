import useSWR from 'swr'
import {
	cvFetcher,
	fetcher,
	multiFetcherAll,
	multiFetcherArena,
	multiFetcherDokarkiv,
	multiFetcherInst,
	multiFetcherPensjon,
} from '@/api'
import {
	useArenaEnvironments,
	useDokarkivEnvironments,
	useInstEnvironments,
	usePensjonEnvironments,
} from '@/utils/hooks/useEnvironments'

import { useTransaksjonsid } from '@/utils/hooks/useTransaksjonsid'

const poppUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/testnav-dolly-proxy/pensjon/api/v1/inntekt?fnr=${ident}&miljo=${miljo}`,
		miljo: miljo,
	}))

const pensjonsavtaleUrl = (miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/testnav-dolly-proxy/pensjon/api/v2/pensjonsavtale/hent?miljo=${miljo}`,
		miljo: miljo,
	}))

const tpForholdUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/testnav-dolly-proxy/pensjon/api/v1/tp/forhold?fnr=${ident}&miljo=${miljo}`,
		miljo: miljo,
	}))

const tpYtelseUrl = (miljo, ordningNr) =>
	`/testnav-dolly-proxy/pensjon/api/v1/tp/ytelse?ordning=${ordningNr}&miljo=${miljo}`

const instUrl = (ident, miljoer) =>
	miljoer?.map((miljo) => ({
		url: `/testnav-dolly-proxy/inst/api/v1/institusjonsopphold/person?environments=${miljo}`,
		miljo: miljo,
	}))

const arenaUrl = (miljoer) =>
	miljoer?.map((miljoe) => ({
		url: `/testnav-arena-forvalteren-proxy/${miljoe}/arena/syntetiser/brukeroppfolging/personstatusytelse`,
		miljo: miljoe,
	}))

const transaksjonIdUrl = (ident, system) =>
	`/dolly-backend/api/v1/transaksjonid?ident=${ident}&system=${system}`

const journalpostUrl = (transaksjonsid, miljoer) => {
	const urlListe = []
	miljoer.forEach((miljoe) => {
		const journalpostId = transaksjonsid
			?.filter((id) => id.miljoe === miljoe)
			?.flatMap((filtrertId) => filtrertId?.transaksjonId?.map((item) => item?.journalpostId))
		if (journalpostId && journalpostId?.length > 0) {
			journalpostId?.forEach((journalpost) => {
				urlListe.push({
					url: `/testnav-joark-dokument-service/api/v2/journalpost/${journalpost}`,
					miljo: miljoe,
				})
			})
		} else {
			urlListe.push({
				url: null,
				miljo: miljoe,
			})
		}
	})
	return urlListe
}

const histarkUrl = (transaksjonsid: any) => {
	const urlListe: string[] = []
	transaksjonsid?.forEach((transaksjon: any) => {
		transaksjon?.transaksjonId?.forEach((tx) => {
			if (tx?.dokumentInfoId) {
				urlListe.push(`/testnav-dolly-proxy/histark/api/saksmapper/${tx.dokumentInfoId}`)
			}
		})
	})
	return urlListe
}

const arbeidsforholdcvUrl = '/testnav-arbeidsplassencv-proxy/rest/v2/cv'

export const usePoppData = (ident, harPoppBestilling) => {
	const { pensjonEnvironments } = usePensjonEnvironments()

	const { data, isLoading, error } = useSWR<any, Error>(
		[
			harPoppBestilling ? poppUrl(ident, pensjonEnvironments) : null,
			{ 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly' },
		],
		([url, headers]) => multiFetcherPensjon(url, headers),
	)

	return {
		poppData: data?.sort?.((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: isLoading,
		error: error,
	}
}

export const usePensjonsavtaleData = (ident, harPensjonsavtaleBestilling) => {
	const { pensjonEnvironments } = usePensjonEnvironments()

	const { data, isLoading, error } = useSWR<any, Error>(
		[
			harPensjonsavtaleBestilling ? pensjonsavtaleUrl(pensjonEnvironments) : null,
			{ 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly', ident: ident },
		],
		([url, headers]) => multiFetcherPensjon(url, headers),
	)

	return {
		pensjonsavtaleData: data?.sort?.((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: isLoading,
		error: error,
	}
}

export const useTpDataForhold = (ident, harTpBestilling) => {
	const { pensjonEnvironments } = usePensjonEnvironments()

	const { data, isLoading, error } = useSWR<any, Error>(
		[
			harTpBestilling ? tpForholdUrl(ident, pensjonEnvironments) : null,
			{ 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly' },
		],
		([url, headers]) => multiFetcherPensjon(url, headers),
	)

	return {
		tpDataForhold: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: isLoading,
		error: error,
	}
}

export const useTpDataYtelse = (ident, ordningNr, miljo) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		[
			ordningNr ? tpYtelseUrl(miljo, ordningNr) : null,
			{ 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly', fnr: ident },
		],
		([url, headers]) => fetcher(url, headers),
	)

	return {
		tpDataYtelse: data,
		loading: isLoading,
		error: error,
	}
}

export const useTransaksjonIdData = (ident, system, harBestilling, fagsystemMiljoer = null) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		harBestilling ? transaksjonIdUrl(ident, system) : null,
		fetcher,
	)

	const getMiljoData = () => {
		if (!harBestilling || !data) {
			return null
		}
		const miljoData = []
		if (fagsystemMiljoer && fagsystemMiljoer.length > 0) {
			fagsystemMiljoer.map((miljo) => {
				const fagsystemData = data?.find((d) => d?.miljoe === miljo)?.transaksjonId
				miljoData.push({ data: fagsystemData, miljo: miljo })
			})
		} else {
			data?.map((m) => {
				miljoData.push({ data: m.transaksjonId, miljo: m.miljoe })
			})
		}
		return miljoData
	}
	const miljoData = getMiljoData()

	return {
		data: miljoData?.sort?.((a, b) => a.miljo?.localeCompare(b.miljo)),
		loading: isLoading,
		error: error,
	}
}

export const useTransaksjonIdPensjon = (ident, harBestilling) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		harBestilling ? transaksjonIdUrl(ident, 'PEN_AP') : null,
		fetcher,
	)
	const getMiljoData = () => {
		if (!harBestilling || !data) {
			return null
		}
		const miljoData = []
		data?.forEach((m) => {
			miljoData.push({ data: m, miljo: m.miljoe })
		})
		return miljoData
	}
	const miljoData = getMiljoData()

	return {
		data: miljoData?.sort?.((a, b) => a.miljo?.localeCompare(b.miljo)),
		loading: isLoading,
		error: error,
	}
}

export const useInstData = (ident, harInstBestilling) => {
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

export const useDokarkivData = (ident, harDokarkivbestilling) => {
	const { transaksjonsid } = useTransaksjonsid('DOKARKIV', ident)
	const { dokarkivEnvironments } = useDokarkivEnvironments()

	const { data, isLoading, error } = useSWR<any, Error>(
		harDokarkivbestilling ? journalpostUrl(transaksjonsid, dokarkivEnvironments) : null,
		multiFetcherDokarkiv,
	)

	return {
		dokarkivData: data?.filter((journalpost) => journalpost.data?.journalpostId !== null),
		loading: isLoading,
		error: error,
	}
}

export const useHistarkData = (ident, harHistarkbestilling) => {
	const { transaksjonsid } = useTransaksjonsid('HISTARK', ident)
	const { data, isLoading, error } = useSWR<any, Error>(
		harHistarkbestilling ? histarkUrl(transaksjonsid) : null,
		multiFetcherAll,
	)

	return {
		histarkData: data,
		loading: isLoading,
		error: error,
	}
}

export const useArbeidsplassencvData = (ident: string, harArbeidsplassenBestilling: boolean) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		[harArbeidsplassenBestilling ? arbeidsforholdcvUrl : null, { fnr: ident }],
		([url, headers]) => cvFetcher(url, headers),
	)

	return {
		arbeidsplassencvData: data,
		loading: isLoading,
		error: error,
	}
}

export const useArenaData = (ident: string, harArenaBestilling: boolean) => {
	const { arenaEnvironments } = useArenaEnvironments()

	const { data, isLoading, error } = useSWR<any, Error>(
		[harArenaBestilling ? arenaUrl(arenaEnvironments) : null, { fodselsnr: ident }],
		([url, headers]) => multiFetcherArena(url, headers),
	)

	return {
		arenaData: data?.sort?.((a, b) => a.miljo?.localeCompare(b.miljo)),
		loading: isLoading,
		error: error,
	}
}

import useSWR from 'swr'
import { fetcher, imageFetcher } from '@/api'

export type Journalpost = {
	journalpostId: number
	tittel: string
	avsenderMottaker: AvsenderMottaker
	tema: string
	behandlingstema: string
	behandlingstemanavn: string
	kanal: string
	journalfoerendeEnhet: string
	sak: Sak
	dokumenter: Dokument[]
	miljoe: string
	bestillingId?: number
}

export type Dokument = {
	dokumentInfoId: number
	tittel?: string
}

type AvsenderMottaker = {
	id: string
	navn: string
	type: string
}

type Sak = {
	sakstype: string
	fagsaksystem: string
	fagsakId: string
}

export const useDokumenterFraMal = (malId: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		malId ? `/dolly-backend/api/v1/dokument/mal/${malId}` : null,
		fetcher,
	)

	return {
		dokumenter: data,
		loading: isLoading,
		error: error,
	}
}

export const useJoarkPDF = (journalpostId?: number, dokumentInfoId?: number, miljo?: string) => {
	const { data, isLoading, error } = useSWR<Blob, Error>(
		journalpostId && dokumentInfoId && miljo
			? `/testnav-joark-dokument-service/api/v2/journalpost/${journalpostId}/dokumenter/${dokumentInfoId}/pdf`
			: null,
		fetcher,
		{
			revalidateOnFocus: false,
			headers: {
				miljo,
				Accept: 'application/pdf',
				'Content-Type': 'application/pdf',
			},
		},
	)

	return {
		pdf: data,
		loading: isLoading,
		error,
	}
}

export const useHistarkPDF = (dokumentInfoId?: number) => {
	const { data, isLoading, error } = useSWR<Blob, Error>(
		dokumentInfoId ? `/testnav-histark-proxy/api/saksmapper/${dokumentInfoId}/pdf` : null,
		imageFetcher,
		{
			revalidateOnFocus: false,
			headers: {
				Accept: 'application/pdf',
				'Content-Type': 'application/pdf',
			},
		},
	)

	return {
		pdfURL: data,
		loading: isLoading,
		error,
	}
}

import api from '@/api'

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
	ferdigstill?: boolean
	vedlegg?: any[]
}

export type Dokument = {
	dokumentInfoId: number
	tittel?: string
	brevkode?: string
}

type AvsenderMottaker = {
	id: string
	idType?: string
	navn: string
	type: string
}

type Sak = {
	sakstype: string
	fagsaksystem: string
	fagsakId: string
}

type DokumentType = 'ORIGINAL' | 'ARKIV'

const hentJournalpost = (journalpostId: number, miljo: string): Promise<Journalpost> =>
	api.fetchJson(`/testnav-joark-dokument-service/api/v2/journalpost/${journalpostId}`, {
		method: 'GET',
		headers: { miljo: miljo },
	})

const hentDokument = (
	journalpostId: number,
	dokumentInfoId: number,
	miljo: string,
	dokumentType: DokumentType,
): Promise<string> =>
	api
		.fetch(
			`/testnav-joark-dokument-service/api/v2/journalpost/${journalpostId}/dokumenter/${dokumentInfoId}?dokumentType=${dokumentType}`,
			{
				method: 'GET',
				headers: { miljo: miljo },
			},
		)
		.then((response) => response.text())

const hentPDF = (journalpostId: number, dokumentInfoId: number, miljo: string): any =>
	api
		.fetch(
			`/testnav-joark-dokument-service/api/v2/journalpost/${journalpostId}/dokumenter/${dokumentInfoId}/pdf`,
			{
				method: 'GET',
				headers: {
					miljo: miljo,
					Accept: 'application/pdf',
					'Content-Type': 'application/pdf',
				},
			},
		)
		.then((response) => {
			return response.blob()
		})
		.then((resp) => {
			const fileURL = URL.createObjectURL(resp)
			window.open(fileURL)
		})
		.catch((error) => {
			console.error(error)
		})

export default {
	hentJournalpost,
	hentDokument,
	hentPDF,
}

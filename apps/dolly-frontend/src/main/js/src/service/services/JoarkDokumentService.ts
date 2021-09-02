import api from '@/api'

export type Journalpost = {
	journalpostId: number
	tittel: string
	avsenderMottaker: AvsenderMottaker
	tema: string
	kanal: string
	dokumenter: Dokument[]
}

export type Dokument = {
	dokumentInfoId: number
	tittel: string
}

type AvsenderMottaker = {
	id: string
	navn: string
	type: string
}

type DokumentType = 'ORIGINAL' | 'ARKIV'

const hentJournalpost = (journalpostId: number, miljo: string): Promise<Journalpost> =>
	api.fetchJson(`/testnav-joark-dokument-service/api/v2/journalpost/${journalpostId}`, {
		method: 'GET',
		headers: { miljo: miljo }
	})

const hentDokument = (
	journalpostId: number,
	dokumentInfoId: number,
	miljo: string,
	dokumentType: DokumentType
): Promise<string> =>
	api
		.fetch(
			`/testnav-joark-dokument-service/api/v2/journalpost/${journalpostId}/dokumenter/${dokumentInfoId}?dokumentType=${dokumentType}`,
			{
				method: 'GET',
				headers: { miljo: miljo }
			}
		)
		.then(response => response.text())

const hentPDF = (
	journalpostId: number,
	dokumentInfoId: number,
	miljo: string,
	dokumentType: DokumentType
): any =>
	api
		.fetch(
			`/testnav-joark-dokument-service/api/v2/journalpost/${journalpostId}/dokumenter/${dokumentInfoId}?dokumentType=${dokumentType}/pdf`,
			{
				method: 'GET',
				headers: {
					miljo: miljo,
					Accept: 'application/pdf',
					'Content-Type': 'application/pdf'
				}
			}
		)
		.then(response => {
			return response.blob()
		})
		.then(resp => {
			console.log('resp: ', resp) //TODO - SLETT MEG
			const fileURL = URL.createObjectURL(resp)
			const link = document.createElement('a')
			link.href = fileURL
			link.download = `Dokument-${dokumentInfoId}.pdf`
			link.click()
		})
		.catch(error => {
			console.log(error)
		})

export default {
	hentJournalpost,
	hentDokument,
	hentPDF
}

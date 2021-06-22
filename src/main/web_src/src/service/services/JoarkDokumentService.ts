import api from '@/api'

type DokumentInfo = {
	journalpostId: number
	dokumentInfoId: number
	tittel: string
}

export type Dokument = {
	journalpostId: number
	dokumentInfoId: number
	dokument: string
}

const hentDokument = (
	journalpostId: number,
	dokumentInfoId: number,
	miljo: string
): Promise<string> =>
	api
		.fetch(
			`/testnav-joark-dokument-service/api/v1/journalpost/${journalpostId}/dokumenter/${dokumentInfoId}`,
			{
				method: 'GET',
				headers: { miljo: miljo }
			}
		)
		.then(response => response.text())

const hentDokumenter = (journalpostId: number, miljo: string): Promise<Dokument[]> => {
	return api
		.fetchJson(`/testnav-joark-dokument-service/api/v1/journalpost/${journalpostId}/dokumenter`, {
			method: 'GET',
			headers: { miljo: miljo }
		})
		.then((response: DokumentInfo[]) =>
			Promise.all(
				response.map(dokument =>
					hentDokument(dokument.journalpostId, dokument.dokumentInfoId, miljo).then(response => ({
						journalpostId: dokument.journalpostId,
						dokumentInfoId: dokument.dokumentInfoId,
						dokument: response
					}))
				)
			)
		)
}

export default {
	hentDokumenter
}

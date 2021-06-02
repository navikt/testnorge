import Api from '@/api'

export type Organisasjon = {
	orgnummer: string
	enhetstype: string
	navn: string
	redigertNavn?: string
	epost?: string
	internetAdresse?: string
	naeringskode?: string
	overenhet?: string
	forretningsAdresse?: Adresse
	postadresse?: Adresse
	opprinnelse?: string
	tags: string[]
}

export type Adresse = {
	adresselinje1?: string
	adresselinje2?: string
	adresselinje3?: string
	postnr?: string
	kommunenr?: string
	landkode?: string
	poststed?: string
}

const fetchOrganisasjoner = (gruppe: string, kanHaArbeidsforhold: boolean = null) =>
	Api.fetchJson<Organisasjon[]>(
		`/testnav-organisasjon-faste-data-service/api/v1/organisasjoner?gruppe=${gruppe}${
			kanHaArbeidsforhold !== null ? '&kanHaArbeidsforhold=' + kanHaArbeidsforhold : ''
		}`,
		{
			method: 'GET'
		}
	)

export default { fetchOrganisasjoner }

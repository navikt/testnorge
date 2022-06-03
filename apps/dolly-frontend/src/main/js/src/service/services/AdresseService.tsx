import api from '@/api'

type AdresseRequest = {
	matrikkelId?: string
	adressenavn?: string
	husnummer?: number
	husbokstav?: string
	postnummer?: string
	poststed?: string
	kommunenummer?: string
	kommunenavn?: string
	bydelsnummer?: string
	bydelsnavn?: string
	tilleggsnavn?: string
	fritekst?: string
}

type MatrikkelAdresseRequest = {
	matrikkelId?: string
	kommunenummer?: string
	gaardsnummer?: string
	bruksnummer?: string
	postnummer?: string
	poststed?: string
	tilleggsnavn?: string
}

export type Adresse = {
	matrikkelId: string
	adressekode: string
	adressenavn: string
	husnummer: number
	husbokstav?: string
	postnummer: string
	poststed: string
	kommunenummer: string
	kommunenavn: string
	bydelsnummer?: string
	bydelsnavn?: string
	tilleggsnavn?: string
	fylkesnummer: string
	fylkesnavn: string
	bruksenhetsnummer?: string
}

export type MatrikkelAdresse = {
	matrikkelId: string
	kommunenummer: string
	gaardsnummer: string
	bruksnummer: string
	postnummer: string
	poststed: string
	bruksenhetsnummer?: string
	tilleggsnavn: string
}

const getQueryParms = (request: any) => {
	const keys = Object.keys(request).filter(
		(key: keyof Request) => request[key] && request[key] !== ''
	)
	if (keys.length === 0) {
		return ''
	}
	return '?' + keys.map((key: keyof Request) => `${key}=${request[key]}`).join('&')
}

export default {
	hentAdresser(request: AdresseRequest, antall: number = 1): Promise<Adresse[]> {
		return api.fetchJson(`/testnav-adresse-service/api/v1/adresser/veg${getQueryParms(request)}`, {
			method: 'GET',
			headers: { antall: antall.toString() },
		})
	},
	hentMatrikkelAdresser(
		request: MatrikkelAdresseRequest,
		antall: number = 10
	): Promise<MatrikkelAdresse[]> {
		return api.fetchJson(
			`/testnav-adresse-service/api/v1/adresser/matrikkeladresse${getQueryParms(request)}`,
			{
				method: 'GET',
				headers: { antall: antall.toString() },
			}
		)
	},
}

import api from '@/api'

type Request = {
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
}

export default {
	hentAdresser(request: Request, antall: number = 1): Promise<Adresse[]> {
		const getQueryParms = () => {
			const keys = Object.keys(request).filter(
				(key: keyof Request) => request[key] && request[key] !== ''
			)
			if (keys.length === 0) {
				return ''
			}
			return '?' + keys.map((key: keyof Request) => `${key}=${request[key]}`).join('&')
		}

		return api.fetchJson(`/testnav-adresse-service/api/v1/adresser/veg${getQueryParms()}`, {
			method: 'GET',
			headers: { antall: antall.toString() }
		})
	}
}

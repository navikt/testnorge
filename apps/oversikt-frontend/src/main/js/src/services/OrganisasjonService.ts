import { Api } from '@navikt/dolly-lib'

export type Organisasjon = {
	navn: string
	organisasjonsnummer: string
	organisasjonsfrom: string
	gyldigTil: string
}
const getOrganisasjoner = (): Promise<Organisasjon[]> =>
	Api.fetchJson('/testnav-person-organisasjon-tilgang-service/api/v1/person/organisasjoner', {
		method: 'GET',
	})

export default { getOrganisasjoner }

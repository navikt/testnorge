import { Api } from '@navikt/dolly-lib'

export type Organisasjon = {
	navn: string
	organisasjonsnummer: string
	organisasjonsform: string
}
const getOrganisasjoner = (): Promise<Organisasjon[]> =>
	Api.fetchJson('/api/v1/person-organisasjoner', {
		method: 'GET',
	})

export default { getOrganisasjoner }

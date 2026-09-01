import { Api } from '@navikt/dolly-lib'

export type Organisasjon = {
	navn: string
	organisasjonsnummer: string
	organisasjonsform: string
}

let organisasjonsnummer = ''

const getOrganisasjoner = (): Promise<Organisasjon[]> =>
	Api.fetchJson('/api/v1/person-organisasjoner', {
		method: 'GET',
	})

const setOrganisasjonsnummer = (value: string) => {
	organisasjonsnummer = value
}

const getOrganisasjonsnummer = () => organisasjonsnummer

export default { getOrganisasjoner, setOrganisasjonsnummer, getOrganisasjonsnummer }

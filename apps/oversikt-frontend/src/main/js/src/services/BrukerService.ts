import { Api } from '@navikt/dolly-lib'

export type Bruker = {
	id: string
	brukernavn: string
	organisasjonsnummer: string
	opprettet: string
	sistInnlogget: string
}

const getBruker = (orgnummer: string) =>
	Api.fetchJson<Bruker>(`testnav-bruker-service/api/v1/brukere?organisasjonsnummer=${orgnummer}`, {
		method: 'GET',
	})

export default {
	getBruker,
}

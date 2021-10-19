import { Api } from '@navikt/dolly-lib'

export type Bruker = {
	id: string
	brukernavn: string
	organisasjonsnummer: string
	opprettet: string
	sistInnlogget: string
}

const getBruker = (orgnummer: string) =>
	Api.fetchJson<Bruker[]>(
		`testnav-bruker-service/api/v1/brukere?organisasjonsnummer=${orgnummer}`,
		{
			method: 'GET',
		}
	).then((brukere) => brukere[0])

const getToken = (id: string) =>
	Api.fetch(`testnav-bruker-service/api/v1/brukere/${id}/token`, {
		method: 'POST',
	}).then((response) => response.text())

export default {
	getBruker,
	getToken,
}

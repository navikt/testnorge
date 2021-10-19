import { Api } from '@navikt/dolly-lib'

export type Organisasjon = {
	navn: string
	organisasjonsnummer: string
	organisasjonsfrom: string
	gyldigTil: string
}

const addToSession = (orgnummer: string) =>
	Api.fetch(`/api/v1/session/user?organisasjonsnummer=${orgnummer}`, { method: 'PUT' })

const getOrganisasjoner = (): Promise<Organisasjon[]> =>
	Api.fetchJson('/testnav-person-organisasjon-tilgang-service/api/v1/person/organisasjoner', {
		method: 'GET',
	})
//
// const getOrganisasjoner = (): Promise<Organisasjon[]> =>
// 	new Promise((resolve) => {
// 		setTimeout(
// 			resolve.bind(null, [
// 				{
// 					navn: 'Test',
// 					organisasjonsnummer: '999999999',
// 					organisasjonsfrom: 'AS',
// 					gyldigTil: '',
// 				},
// 				{
// 					navn: 'Dolly Dollysen',
// 					organisasjonsnummer: '888888888',
// 					organisasjonsfrom: 'AS',
// 					gyldigTil: '',
// 				},
// 			]),
// 			2000
// 		)
// 	})

export default { addToSession, getOrganisasjoner }

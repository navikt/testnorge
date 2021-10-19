import { Api } from '@navikt/dolly-lib'

const addToSession = (orgnummer: string) =>
	Api.fetch(`/api/v1/session/user?organisasjonsnummer=${orgnummer}`, { method: 'PUT' })

const clear = () => Api.fetch('/api/v1/session/user', { method: 'DELETE' })

export default {
	addToSession,
	clear,
}

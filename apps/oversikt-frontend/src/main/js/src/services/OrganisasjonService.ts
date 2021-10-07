import { Api } from '@navikt/dolly-lib'

const setOrganisasjonsnummer = (orgnummer: string) =>
	Api.fetch(`/api/v1/organisasjon/${orgnummer}`, { method: 'PUT' })

export default { setOrganisasjonsnummer }

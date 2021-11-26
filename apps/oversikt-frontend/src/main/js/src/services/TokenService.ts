import { Api } from '@navikt/dolly-lib'
import { Application } from '@/services/ApplicationService'

const fetchToken = (application: Application, clientCredentials: boolean = false) =>
	Api.fetchJson<{ token: string }>(
		`/api/v1/token?clientCredentials=${clientCredentials}`,
		{
			method: 'POST',
		},
		JSON.stringify(application)
	)

const fetchMagicToken = () =>
	Api.fetchJson<{ token: string }>(`/api/v1/token`, {
		method: 'POST',
	})

export default { fetchToken, fetchMagicToken }

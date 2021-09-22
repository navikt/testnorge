import { Api } from '@navikt/dolly-lib'
import { Application } from '@/services/ApplicationService'

const fetchToken = (application: Application) =>
	Api.fetchJson<{ token: string }>(
		`/api/v1/token`,
		{
			method: 'POST',
		},
		application
	)

const fetchMagicToken = () =>
	Api.fetchJson<{ token: string }>(`/api/v1/token`, {
		method: 'POST',
	})

export default { fetchToken, fetchMagicToken }

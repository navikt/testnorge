import { Api } from '@navikt/dolly-lib'

export type Application = {
	cluster: string
	namespace: string
	name: string
}

const fetchApplications = () =>
	Api.fetchJson<Application[]>('/api/v1/applications', { method: 'GET' })

export default { fetchApplications }

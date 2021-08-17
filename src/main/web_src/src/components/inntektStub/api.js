import Api from '~/api'

const uri = `/testnav-inntektstub-proxy/api/v1/inntektstub`

export const validate = values => Api.fetchJson(`${uri}/valider`, { method: 'POST' }, values)

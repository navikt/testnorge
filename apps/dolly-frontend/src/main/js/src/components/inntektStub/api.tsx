import Api from '@/api'

const uri = `/testnav-dolly-proxy/inntektstub/api/v1/inntektstub`

export const validate = (values) => Api.fetchJson(`${uri}/valider`, { method: 'POST' }, values)

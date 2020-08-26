import config from '~/config'
import Api from '~/api'

const uri = `${config.services.proxyBackend}/inntektstub`

export const validate = values => Api.fetchJson(`${uri}/valider`, { method: 'POST' }, values)

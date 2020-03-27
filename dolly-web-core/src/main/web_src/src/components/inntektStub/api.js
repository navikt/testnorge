import config from '~/config'
import Api from '~/api';

const uri = `${config.services.dollyBackend}/proxy/inntektstub`

export const validate = values => Api.fetchJson(`${uri}/valider`, "POST", values)
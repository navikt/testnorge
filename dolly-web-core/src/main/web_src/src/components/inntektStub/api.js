import config from '~/config'
import Api from '~/api';

const uri = `${config.services.dollyBackend}/dolly`

export const validate = values => Api.fetchJson(`${uri}/inntektstub`, "POST", values)
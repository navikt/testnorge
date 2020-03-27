import config from '~/config'
import Request from '~/service/services/Request'

const getPdlUrl = () => `${config.services.dollyBackend}/proxy/pdl`

const options = {
    headers: {
        'Nav-Call-Id': 'dolly-frontend',
        'Nav-Consumer-Id': 'dolly'
    }
}

export default {
    getPerson(ident) {
        const endpoint = `${getPdlUrl()}/inntektsinformasjon/?norske-identer=${ident}`
        return Request.getWithoutCredentials(endpoint, options)
    }
}
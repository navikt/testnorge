import config from '~/config'
import Request from '~/service/services/Request'

const getInntektstubUrl = () => `${config.services.dollyBackend}/proxy/inntektstub`

const options = {
    headers: {
        'Nav-Call-Id': 'dolly-frontend',
        'Nav-Consumer-Id': 'dolly'
    }
}

export default {
    getInntektsinformasjon(ident) {
        const endpoint = `${getInntektstubUrl()}/inntektsinformasjon/?norske-identer=${ident}`
        return Request.getWithoutCredentials(endpoint, options)
    }
}

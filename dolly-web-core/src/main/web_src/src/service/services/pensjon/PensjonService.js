import config from '~/config'
import Request from '~/service/services/Request'

const getPensjonUrl = () => `${config.services.dollyBackend}/proxy/popp`

const options = {
    headers: {
        'Nav-Call-Id': 'dolly-frontend',
        'Nav-Consumer-Id': 'dolly'
    }
}

export default {
    getPoppInntekt(ident, miljoe) {
        const endpoint = `${getPensjonUrl()}/inntekt?fnr=${ident}&miljo=${miljoe}`
        return Request.getWithoutCredentials(endpoint, options)
    },

    getTilgjengeligeMiljoer() {
        const endpoint = `${getPensjonUrl()}/miljo`
        return Request.getWithoutCredentials(endpoint, options)
    }
}

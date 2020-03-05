import config from '~/config'
import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getPensjonBaseUrl = () => ConfigService.getDatesourceUrl('pensjon')

const options = {
    headers: {
        'Nav-Call-Id': 'dolly-frontend',
        'Nav-Consumer-Id': 'dolly'
    }
}

export default {
    getPerson(ident, env) {
        const endpoint = `${getPensjonBaseUrl()}/popp/inntekt/${ident}/${env}`
        return Request.getWithoutCredentials(endpoint, options)
    },

    getTilgjengeligeMiljoer() {
        const endpoint = `${getPensjonBaseUrl()}/popp/miljoe`
        return Request.getWithoutCredentials(endpoint)
    }
}

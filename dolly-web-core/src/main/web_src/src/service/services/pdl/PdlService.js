import config from '~/config'
import Request from '~/service/services/Request'

const getPdlPersonUrl = () => `${config.services.proxyBackend}/pdlperson`

const options = {
    headers: {
        'Nav-Call-Id': 'dolly-frontend',
        'Nav-Consumer-Id': 'dolly'
    }
}

export default {
    getAPdlPerson(ident) {
        const endpoint = `${getPdlPersonUrl()}/${ident}`
        return Request.getWithoutCredentials(endpoint, options)
    }
}
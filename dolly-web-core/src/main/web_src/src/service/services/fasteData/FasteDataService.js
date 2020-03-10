import config from '~/config'
import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getFasteDateBaseUrl = () => ConfigService.getDatesourceUrl('fasteData')

export default {
    getFasteDataTps() {
        const endpoint = getFasteDateBaseUrl() + '/faste-data/tps'
        return Request.getWithoutCredentials(endpoint)
    }
}

import config from '~/config'
import Request from '~/service/services/Request'

const getHodejegerenUrl = () => `${config.services.proxyBackend}/hodejegeren`

export default {
	soek(soekOptions:string) {
		const endpoint = `${getHodejegerenUrl()}/historikk/soek/${soekOptions}?kilder=skd&pageNumber=0&pageSize=100`
		return Request.get(endpoint)
	}
}

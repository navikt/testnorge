import config from '~/config'
import Request from '~/service/services/Request'

const getHodejegerenUrl = () => `${config.services.proxyBackend}/hodejegeren`

export default {
	soek(soekOptions: string, antallResultat: number) {
		if (!antallResultat) antallResultat = 20
		const endpoint = `${getHodejegerenUrl()}/historikk/soek/${soekOptions}?kilder=skd&pageNumber=0&pageSize=${antallResultat}`
		return Request.get(endpoint)
	}
}

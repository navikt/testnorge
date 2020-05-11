import config from '~/config'
import Request from '~/service/services/Request'
import { HodejegerenResponse, ResponseData } from '~/pages/soekMiniNorge/hodejegeren/types'

const getHodejegerenUrl = () => `${config.services.proxyBackend}/hodejegeren`

export default {
	soek(soekOptions: string, antallResultat: number): Promise<any[]> {
		if (!antallResultat) antallResultat = 20
		const endpoint = `${getHodejegerenUrl()}/historikk/soek/${soekOptions}?kilder=skd&pageNumber=0&pageSize=${antallResultat}`

		return Request.get(endpoint).then((response: HodejegerenResponse) => {
			if (response.data.length > 0) {
				return response.data.map(function(res: ResponseData) {
					return res.kilder[0].data[0].innhold
				})
			}
			return null
		})
	}
}

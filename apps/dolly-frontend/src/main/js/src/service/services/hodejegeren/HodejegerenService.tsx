import Request from '~/service/services/Request'
import { HodejegerenResponse, ResponseData } from '~/pages/soekMiniNorge/hodejegeren/types'

const MIN_SEARCH_RESULTS = 20

export default {
	soek(soekOptions: string, antallResultat: number): Promise<any[]> {
		if (!antallResultat) antallResultat = MIN_SEARCH_RESULTS
		const endpoint = `/testnav-hodejegeren-proxy/api/v1/historikk/soek/${soekOptions}?kilder=skd&pageNumber=0&pageSize=${antallResultat}`

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

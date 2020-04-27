import config from '~/config'
import Request from '~/service/services/Request'

const getBrregstubUrl = () => `${config.services.proxyBackend}/brregstub`

export default {
	getRoller() {
		const endpoint = `${getBrregstubUrl()}/kode/roller`
		return Request.get(endpoint)
	},

	getUnderstatus() {
		const endpoint = `${getBrregstubUrl()}/kode/understatus`
		return Request.get(endpoint)
	}
}

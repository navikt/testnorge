import config from '~/config'
import Request from '~/service/services/Request'

const getProfilUrl = () => `${config.services.proxyBackend}/profil`

export default {
	getProfil() {
		const endpoint = `${getProfilUrl()}/profil`
		return Request.get(endpoint)
	},

	getProfilBilde() {
		const endpoint = `${getProfilUrl()}/profil/bilde`
		return Request.getBilde(endpoint)
	}
}

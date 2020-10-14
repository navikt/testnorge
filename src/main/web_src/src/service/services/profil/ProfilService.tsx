import Request from '~/service/services/Request'

const getProfilUrl = '/api/testnorge-profil-api/v1'

export default {
	getProfil() {
		const endpoint = getProfilUrl + '/profil'
		return Request.get(endpoint)
	},

	getProfilBilde() {
		const endpoint = getProfilUrl + '/profil/bilde'
		return Request.getBilde(endpoint)
	}
}

import Request from '~/service/services/Request'

const getProfilUrl = '/api/testnorge-profil-api'

export default {
	getProfil() {
		const endpoint = getProfilUrl + '/profil'
		return Request.get(endpoint).then(response => {
			if (response != null) return response
		})
	},

	getProfilBilde() {
		const endpoint = getProfilUrl + '/profil/bilde'
		return Request.getBilde(endpoint).then(response => {
			if (response != null) return response
		})
	}
}

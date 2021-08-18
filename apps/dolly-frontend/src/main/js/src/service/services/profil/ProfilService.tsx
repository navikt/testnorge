import Request from '~/service/services/Request'

const getProfilUrl = '/testnorge-profil-api/api/v1'

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

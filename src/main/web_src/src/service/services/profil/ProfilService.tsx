import Request from '~/service/services/Request'

const profilUrl = 'api/testnorge-profil-api/v1/profil'

export default {
	getProfil() {
		return Request.get(profilUrl)
	},

	getProfilBilde() {
		const endpoint = `${profilUrl}/bilde`
		return Request.getBilde(endpoint)
	}
}

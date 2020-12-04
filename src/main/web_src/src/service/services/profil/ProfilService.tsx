import Request from '~/service/services/Request'
import Logger from '~/logger'

const getProfilUrl = '/api/testnorge-profil-api'

function logError(error: any) {
	Logger.error({
		event: `Profil API feilet`,
		message: error.message
	})
}

export default {
	getProfil() {
		const endpoint = getProfilUrl + '/profil'
		return Request.get(endpoint)
			.then(response => {
				if (response != null) return response
			})
			.catch(error => logError(error))
	},

	getProfilBilde() {
		const endpoint = getProfilUrl + '/profil/bilde'
		return Request.getBilde(endpoint)
			.then(response => {
				if (response != null) return response
			})
			.catch(error => logError(error))
	}
}

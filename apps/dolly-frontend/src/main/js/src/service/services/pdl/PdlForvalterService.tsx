import Request from '~/service/services/Request'

const getPdlUrl = () => '/testnav-pdl-forvalter/api/v1'

export default {
	getPersoner(identListe: any) {
		console.log('identListe', identListe)
		if (!identListe) return
		const endpoint = `${getPdlUrl()}/personer?identer=${identListe}`
		// const endpoint = `${getPdlUrl()}/personer`
		return Request.get(endpoint)
		// return Request.get(endpoint, identListe)
	},
}

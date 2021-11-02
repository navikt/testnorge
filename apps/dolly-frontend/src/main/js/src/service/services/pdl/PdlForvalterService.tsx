import Request from '~/service/services/Request'

const getPdlUrl = () => '/testnav-pdl-forvalter/api/v1'

export default {
	getPersoner(identListe: any) {
		if (!identListe) return
		const endpoint = `${getPdlUrl()}/personer?identer=${identListe}`
		return Request.get(endpoint)
	},
}

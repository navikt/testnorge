import Request from '~/service/services/Request'

const getPdlUrl = () => '/testnav-pdl-forvalter/api/v1'

export default {
	getPersoner(identListe: [string]) {
		if (!identListe) return
		const endpoint = `${getPdlUrl()}/personer?identer=${identListe}`
		return Request.get(endpoint)
	},
	soekPersoner(fragment: string) {
		if (!fragment) return
		const endpoint = `${getPdlUrl()}/identiteter?fragment=${fragment}`
		return Request.get(endpoint)
	},
}

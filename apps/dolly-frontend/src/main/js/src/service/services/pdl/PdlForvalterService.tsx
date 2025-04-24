import Request from '@/service/services/Request'

const getPdlUrl = () => '/testnav-pdl-forvalter/api/v1'

export default {
	getPersoner(identListe: string[]) {
		if (!identListe) {
			return
		}
		const endpoint = `${getPdlUrl()}/personer?identer=${identListe}`
		return Request.get(endpoint)
	},
	soekPersoner(fragment: string) {
		if (!fragment) {
			return null
		}
		const endpoint = `${getPdlUrl()}/identiteter?fragment=${fragment}`
		return Request.get(endpoint)
	},
	getEksistens(identListe: [string]) {
		if (!identListe) {
			return
		}
		const endpoint = `${getPdlUrl()}/eksistens?identer=${identListe}`
		return Request.get(endpoint)
	},
	putAttributt(ident: string, attributt: string, id: number, data: any) {
		if (!ident) {
			return
		}
		const attributtId = id ? `/${id}` : ``
		const endpoint = `${getPdlUrl()}/personer/${ident}/${attributt}${attributtId}`
		return Request.putWithoutResponse(endpoint, data)
	},
	deleteAttributt(ident: string, attributt: string, id: number) {
		if (!ident) {
			return
		}
		const endpoint = `${getPdlUrl()}/personer/${ident}/${attributt}/${id}`
		return Request.delete(endpoint)
	},
}

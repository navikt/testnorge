import Request from '@/service/services/Request'

const getPdlUrl = () => '/testnav-pdl-forvalter/api/v1'

const sliceIdentListe = (identListe: string[]) => {
	const maxAntall = 150
	if (identListe.length <= maxAntall) return [identListe]
	const identer = []
	for (let i = 0; i < identListe.length; i += maxAntall) {
		identer.push(identListe.slice(i, i + maxAntall))
	}
	return identer
}

export default {
	getPersoner(identListe: string[]) {
		if (!identListe) {
			return
		}
		const identerDelt = sliceIdentListe(identListe)
		const promises = identerDelt.map((identer) => {
			const endpoint = `${getPdlUrl()}/personer?identer=${identer}`
			return Request.get(endpoint)
		})
		return Promise.all(promises).then((responses) => {
			const data = responses.map((response: any) => response?.data).flat()
			return { data }
		})
	},
	soekPersoner(fragment: string) {
		if (!fragment || fragment.length > 11) {
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
	setStandalone(ident: string, standalone = true) {
		if (!ident) {
			return
		}
		const endpoint = `${getPdlUrl()}/identiteter/${ident}/standalone/${standalone}`
		return Request.putWithoutResponse(endpoint)
	},
}

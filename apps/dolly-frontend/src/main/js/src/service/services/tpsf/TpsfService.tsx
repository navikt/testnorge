import Request from '@/service/services/Request'

const getTpsfUrl = () => `/tps-forvalteren-proxy/api/v1`

export default {
	getPersoner(userArray) {
		if (!userArray) return
		const endpoint = getTpsfUrl() + `/dolly/testdata/hentpersoner`
		return Request.post(endpoint, userArray)
	},

	hentTpsInnhold(identMiljoe) {
		if (!identMiljoe) return
		const endpoint = getTpsfUrl() + '/dolly/testdata/import'
		return Request.post(endpoint, identMiljoe)
	},

	soekPersoner(fragment) {
		if (!fragment || fragment.length > 11) {
			return null
		}
		const endpoint = `${getTpsfUrl()}/dolly/testdata/soekperson?fragment=${fragment}`
		return Request.get(endpoint)
	},

	getMiljoerByFnr(fnr) {
		const endpoint = getTpsfUrl() + '/testdata/tpsStatus?identer=' + fnr
		return Request.get(endpoint)
	},
}

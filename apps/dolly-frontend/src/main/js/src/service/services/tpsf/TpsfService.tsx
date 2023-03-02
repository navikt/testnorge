import Request from '@/service/services/Request'

const getTpsfUrl = () => `/tps-forvalteren-proxy/api/v1`

export default {
	getPersoner(userArray) {
		if (!userArray) return
		const endpoint = getTpsfUrl() + `/dolly/testdata/hentpersoner`
		return Request.post(endpoint, userArray)
	},

	getMiljoerByFnr(fnr) {
		const endpoint = getTpsfUrl() + '/testdata/tpsStatus?identer=' + fnr
		return Request.get(endpoint)
	},
}

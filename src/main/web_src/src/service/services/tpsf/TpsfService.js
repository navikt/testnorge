import Request from '~/service/services/Request'

const getTpsfUrl = () => `/tps-forvalteren-proxy/api/v1`

export default {
	getPersoner(userArray) {
		if (!userArray) return
		const endpoint = getTpsfUrl() + `/dolly/testdata/hentpersoner`

		return Request.post(endpoint, userArray)
	},

	checkpersoner(userArray) {
		if (!userArray) return
		const endpoint = getTpsfUrl() + '/dolly/testdata/checkpersoner'
		return Request.post(endpoint, userArray)
	},

	hentTpsInnhold(identMiljoe) {
		if (!identMiljoe) return
		const endpoint = getTpsfUrl() + '/dolly/testdata/import'
		return Request.post(endpoint, identMiljoe)
	},

	soekPersoner(fragment) {
		if (!fragment) return
		const endpoint = `${getTpsfUrl()}/dolly/testdata/soekperson?fragment=${fragment}`
		return Request.get(endpoint)
	}
}

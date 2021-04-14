import config from '~/config'
import Request from '~/service/services/Request'

const getTpsfUrl = () => `${config.services.proxyBackend}/tpsf`
const getKontaktinfoUrl = () => `${config.services.proxyBackend}/kontaktinfo`

export default {
	getPersoner(userArray) {
		if (!userArray) return
		const endpoint = getTpsfUrl() + `/dolly/testdata/hentpersoner`

		// Må bruke post-request pga maxString-limit på en GET-request
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
	},

	createFoedselsmelding(userData) {
		const endpoint = getTpsfUrl() + '/tpsmelding/foedselsmelding'
		return Request.post(endpoint, userData)
	},

	getKontaktInformasjon(fnr, env) {
		const endpoint =
			getKontaktinfoUrl() + '/tps/kontaktinformasjon?fnr=' + fnr + '&environment=' + env
		return Request.get(endpoint)
	},

	createDoedsmelding(userData) {
		const endpoint = getTpsfUrl() + '/tpsmelding/doedsmelding'
		return Request.post(endpoint, userData)
	},

	getMiljoerByFnr(fnr) {
		const endpoint = getTpsfUrl() + '/testdata/tpsStatus?identer=' + fnr
		return Request.get(endpoint)
	},

	generateRandomAddress() {
		const endpoint = `${getTpsfUrl()}/gyldigadresse/tilfeldig?maxAntall=5`
		return Request.get(endpoint)
	},

	generateAddress(query) {
		const endpoint = `${getTpsfUrl()}/gyldigadresse/autocomplete?maxRetur=5${query}`
		return Request.get(endpoint)
	},

	getTilgjengligeMiljoer() {
		const endpoint = `${getTpsfUrl()}/environments`
		return Request.get(endpoint)
	}
}

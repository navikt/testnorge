import Request from '@/service/services/Request'

const skjermingUrl = '/testnav-skjermingsregister-proxy/api/v1/skjerming/dolly'

export default {
	deleteSkjerming(ident: string) {
		return Request.put(skjermingUrl, { personident: ident, skjermetTil: new Date() })
	},

	getSkjerming(ident: string) {
		return Request.get(skjermingUrl, { personident: ident })
	},
}

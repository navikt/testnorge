import Request from '@/service/services/Request'

const skjermingUrl = '/testnav-skjermingsregister-proxy/api/v1/skjermingdata'

export default {
	deleteSkjerming(ident: string) {
		return Request.delete(`${skjermingUrl}/${ident}`)
	},
}

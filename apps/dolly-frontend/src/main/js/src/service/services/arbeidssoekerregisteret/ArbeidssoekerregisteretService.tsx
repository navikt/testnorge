import Request from '@/service/services/Request'

const baseUrl = '/testnav-arbeidssoekerregisteret-proxy'

const getRegistreringUrl = (ident: string) => `${baseUrl}/api/v1/arbeidssoekerregistrering/${ident}`

export default {
	getRegistreringUrl,
	stoppRegistrering(ident: string) {
		return Request.delete(getRegistreringUrl(ident))
	},
}

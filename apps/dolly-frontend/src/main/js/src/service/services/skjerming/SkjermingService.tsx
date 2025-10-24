import Request from '@/service/services/Request'
import { subDays } from 'date-fns'

const skjermingUrl = '/testnav-dolly-proxy/skjermingsregister/api/v1/skjerming/dolly'

export default {
	deleteSkjerming(ident: string, fornavn: string, etternavn: string) {
		return Request.put(skjermingUrl, {
			personident: ident,
			fornavn: fornavn,
			etternavn: etternavn,
			skjermetFra: subDays(new Date(), 1),
			skjermetTil: new Date(),
		})
	},

	getSkjerming(ident: string) {
		return Request.get(skjermingUrl, { personident: ident })
	},
}

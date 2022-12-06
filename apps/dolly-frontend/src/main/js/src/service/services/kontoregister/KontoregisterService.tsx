import Request from '@/service/services/Request'

const kontoregisterHentUrl = '/testnav-kontoregister-person-proxy/api/system/v1/hent-konto'
const kontoregisterSlettUrl = '/testnav-kontoregister-person-proxy/api/system/v1/slett-konto'

export type KontoregisterData = {
	aktivKonto: {
		kontonummer: string
		utenlandskKontoInfo?: {
			swiftBicKode?: string
			bankLandkode?: string
			banknavn?: string
			valutakode?: string
			bankadresse1?: string
			bankadresse2?: string
			bankadresse3?: string
		}
	}
}

export type PromiseKontoregisterData = {
	data: KontoregisterData
}

export default {
	hentKonto(ident: string): Promise<PromiseKontoregisterData> {
		return Request.post(kontoregisterHentUrl, { kontohaver: ident }).then((response: any) => {
			if (response != null) {
				return response
			}
		})
	},
	slettKonto(ident: string): Promise<PromiseKontoregisterData> {
		return Request.post(kontoregisterSlettUrl, { kontohaver: ident, bestiller: '' }).then(
			(response: any) => {
				if (response != null) {
					return response
				}
			}
		)
	},
}

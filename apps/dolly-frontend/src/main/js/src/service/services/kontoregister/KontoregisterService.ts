import Request from '~/service/services/Request'

const kontoregisterUrl =
	'/testnav-kontoregister-person-proxy/kontoregister/api/kontoregister/v1/hent-konto'

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
		return Request.post(kontoregisterUrl, { kontohaver: ident }).then((response: any) => {
			if (response != null) {
				return response
			}
		})
	},
}

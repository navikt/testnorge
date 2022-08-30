import React, { useCallback, useEffect, useRef, useState } from 'react'
import { TpsMessagingApi, BankkontoApi } from '~/service/Api'

export const TpsMessagingData = (ident: string, environments: Array<string>, loading = false) => {
	const [tpsMessagingData, setTpsMessagingData] = useState(null)
	const [tpsMessagingLoading, setTpsMessagingLoading] = useState(false)
	const mountedRef = useRef(true)

	const execute = useCallback(() => {
		const tpsMessaging = async () => {
			setTpsMessagingLoading(true)
			const tpsApi = TpsMessagingApi.getTpsPersonInfo(ident, environments[0])
				.then((response: any) => {
					return response?.data[0]?.person
				})
				.catch((_e: Error) => {
					return null
				})
			const kontoregisterApi = BankkontoApi.hentKonto(ident)
				.then((response: any) => {
					return response
				})
				.catch((_e: Error) => {
					return null
				})

			const allResponses = await Promise.all([tpsApi, kontoregisterApi])
			const resp = allResponses[0]
			const kontoregisterResp = allResponses[1]

			if (kontoregisterResp?.data?.aktiveKonto) {
				if (kontoregisterResp.data.aktiveKonto.utenlandskKontoInfo) {
					resp.bankkontonrUtland = {
						kontonummer: kontoregisterResp.data.aktiveKonto.kontonummer,
						swift: kontoregisterResp.data.aktiveKonto.utenlandskKontoInfo.swiftBicKode,
						landkode: kontoregisterResp.data.aktiveKonto.utenlandskKontoInfo.bankLandkode,
						banknavn: kontoregisterResp.data.aktiveKonto.utenlandskKontoInfo.banknavn,
						iban: kontoregisterResp.data.aktiveKonto.kontonummer,
						valuta: kontoregisterResp.data.aktiveKonto.utenlandskKontoInfo.valutakode,
						bankAdresse1: kontoregisterResp.data.aktiveKonto.utenlandskKontoInfo.bankadresse1,
						bankAdresse2: kontoregisterResp.data.aktiveKonto.utenlandskKontoInfo.bankadresse2,
						bankAdresse3: kontoregisterResp.data.aktiveKonto.utenlandskKontoInfo.bankadresse3,
					}
				} else {
					resp.bankkontonrNorsk = {
						kontonummer: kontoregisterResp.data.aktiveKonto.kontonummer,
					}
				}
			}

			if (mountedRef.current) {
				setTpsMessagingData(resp)
				setTpsMessagingLoading(false)
			}
		}
		return tpsMessaging()
	}, [environments])

	useEffect(() => {
		if (!loading && environments && environments.length > 0) {
			execute()
		}
		return () => {
			mountedRef.current = false
		}
	}, [])

	return {
		tpsMessagingData: tpsMessagingData,
		tpsMessagingLoading: tpsMessagingLoading,
	}
}

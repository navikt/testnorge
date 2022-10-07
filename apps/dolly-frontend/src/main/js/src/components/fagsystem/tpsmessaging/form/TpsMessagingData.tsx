import React, { useCallback, useEffect, useRef, useState } from 'react'
import { TpsMessagingApi, BankkontoApi } from '~/service/Api'
import { addGlobalError } from '~/ducks/errors'
import { useDispatch } from 'react-redux'

export const TpsMessagingData = (ident: string, environments: Array<string>, loading = false) => {
	const [tpsMessagingData, setTpsMessagingData] = useState(null)
	const [tpsMessagingLoading, setTpsMessagingLoading] = useState(false)
	const mountedRef = useRef(true)
	const dispatch = useDispatch()

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
			const resp = allResponses[0] || {}
			const kontoregisterResp = allResponses[1]

			if (!allResponses[0]) {
				const errorMessage = `Henting av person fra TPS feilet. Dersom Dolly er ustabil, prøv å laste siden på nytt!`
				dispatch(addGlobalError(errorMessage))
			}

			if (kontoregisterResp?.data) {
				if (kontoregisterResp.data.aktivKonto) {
					if (kontoregisterResp.data.aktivKonto.utenlandskKontoInfo) {
						resp.bankkontonrUtland = {
							kontonummer: kontoregisterResp.data.aktivKonto.kontonummer,
							swift: kontoregisterResp.data.aktivKonto.utenlandskKontoInfo.swiftBicKode,
							landkode: kontoregisterResp.data.aktivKonto.utenlandskKontoInfo.bankLandkode,
							banknavn: kontoregisterResp.data.aktivKonto.utenlandskKontoInfo.banknavn,
							iban: kontoregisterResp.data.aktivKonto.kontonummer,
							valuta: kontoregisterResp.data.aktivKonto.utenlandskKontoInfo.valutakode,
							bankAdresse1: kontoregisterResp.data.aktivKonto.utenlandskKontoInfo.bankadresse1,
							bankAdresse2: kontoregisterResp.data.aktivKonto.utenlandskKontoInfo.bankadresse2,
							bankAdresse3: kontoregisterResp.data.aktivKonto.utenlandskKontoInfo.bankadresse3,
						}
					} else {
						resp.bankkontonrNorsk = {
							kontonummer: kontoregisterResp.data.aktivKonto.kontonummer,
						}
					}
				} else {
					resp.bankkontonrUtland = null
					resp.bankkontonrNorsk = null
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

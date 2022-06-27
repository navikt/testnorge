import React, { useCallback, useEffect, useRef, useState } from 'react'
import { TpsMessagingApi } from '~/service/Api'

export const TpsMessagingData = (ident: string, environments: Array<string>, loading = false) => {
	const [tpsMessagingData, setTpsMessagingData] = useState(null)
	const [tpsMessagingLoading, setTpsMessagingLoading] = useState(false)
	const mountedRef = useRef(true)

	const execute = useCallback(() => {
		const tpsMessaging = async () => {
			setTpsMessagingLoading(true)
			const resp = await TpsMessagingApi.getTpsPersonInfo(ident, environments[0])
				.then((response: any) => {
					return response?.data[0]?.person
				})
				.catch((_e: Error) => {
					return null
				})
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

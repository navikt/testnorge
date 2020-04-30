import React, { useState } from 'react'
import { useAsyncFn } from 'react-use'
import { TpsfApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import { SokAdresseForm } from './SokAdresseForm'
import { VelgAdresseForm } from './VelgAdresseForm'

export const GyldigAdresseVelger = ({ settBoadresse, formikBag }) => {
	const [adresser, setAdresser] = useState()
	const [feilmelding, setFeilmelding] = useState()
	const [state, fetch] = useAsyncFn(async query => {
		setAdresser(null)
		setFeilmelding(null)

		const result = query
			? await TpsfApi.generateAddress(query)
			: await TpsfApi.generateRandomAddress()

		const addressData = result.data.response.data1.adrData || []

		if (addressData.length > 0) {
			const addressDataArr = Array.isArray(addressData) ? addressData : [addressData]
			setAdresser(addressDataArr)
		} else {
			const status = result.data.response.status.utfyllendeMelding
			setFeilmelding(status)
		}

		return result
	}, [])

	return (
		<div className="gyldigAdresse">
			<SokAdresseForm onSearch={fetch} formikBag={formikBag}/>
			{state.loading && <Loading label="henter gyldige adresser" />}
			{!feilmelding && adresser && (
				<VelgAdresseForm adresser={adresser} velgAdresse={settBoadresse} />
			)}
			{feilmelding && <Feilmelding feil={feilmelding} />}
		</div>
	)
}

const Feilmelding = ({ feil }) => {
	return (
		<div className="error-message">
			<p>{feil}</p>
		</div>
	)
}

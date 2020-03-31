import React, { createContext } from 'react'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { TpsfApi } from '~/service/Api'
import { StegVelger } from './stegVelger/StegVelger'
import { AppError } from '~/components/ui/appError/AppError'
import { BVOptions } from './options/options'

import './bestillingsveileder.less'

export const BestillingsveilederContext = createContext()

export const Bestillingsveileder = ({ error, location, sendBestilling, match }) => {
	const options = BVOptions(location.state)
	const personId = match.params.personId
	const handleSubmit = (values, formikBag) => {
		sendBestilling(values, options)
	}

	if (error) {
		return <AppError title="Det skjedde en feil ved bestilling" message={error.message} />
	}
	if (personId) {
		return (
			<LoadableComponent
				onFetch={() =>
					TpsfApi.getPersoner([personId]).then(response =>
						response.data.length > 0 ? response.data[0] : null
					)
				}
				render={data => {
					const initialValues = { ...options.initialValues }
					const leggTilPaaPersonOptions = { ...options, personFoerLeggTil: data }
					return renderBestillingsVeileder(initialValues, leggTilPaaPersonOptions, handleSubmit)
				}}
			/>
		)
	}
	return renderBestillingsVeileder(options.initialValues, options, handleSubmit)
}
const renderBestillingsVeileder = (initialValues, options, handleSubmit) => {
	return (
		<div className="bestillingsveileder">
			<BestillingsveilederContext.Provider value={options}>
				<StegVelger initialValues={initialValues} onSubmit={handleSubmit} />
			</BestillingsveilederContext.Provider>
		</div>
	)
}

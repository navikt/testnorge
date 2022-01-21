import React, { createContext } from 'react'
import { StegVelger } from './stegVelger/StegVelger'
import { AppError } from '~/components/ui/appError/AppError'
import { BVOptions } from './options/options'

import './bestillingsveileder.less'

export const BestillingsveilederContext = createContext()

export const Bestillingsveileder = ({ error, gruppeId, location, sendBestilling, match }) => {
	const options = BVOptions(location.state, gruppeId)
	const handleSubmit = (values, formikBag) => {
		sendBestilling(values, options)
	}

	if (error) {
		return <AppError title="Det skjedde en feil ved bestilling" message={error.message} />
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

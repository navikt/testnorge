import React, { createContext } from 'react'
import { StegVelger } from './stegVelger/StegVelger'
import { AppError } from '~/components/ui/appError/AppError'
import { BVOptions } from './options/options'

import './bestillingsveileder.less'
import { useLocation, useNavigate, useParams } from 'react-router-dom'

export const BestillingsveilederContext = createContext()

export const Bestillingsveileder = ({ error, brukertype, brukerId, sendBestilling }) => {
	const location = useLocation()
	const navigate = useNavigate()
	const { gruppeId } = useParams()
	const options = BVOptions(location.state, gruppeId)
	const handleSubmit = (values, formikBag) => {
		sendBestilling(values, options, gruppeId, navigate)
	}

	if (error) {
		return <AppError title="Det skjedde en feil ved bestilling" message={error.message} />
	}
	return renderBestillingsVeileder(
		options.initialValues,
		options,
		brukertype,
		brukerId,
		handleSubmit
	)
}
const renderBestillingsVeileder = (initialValues, options, brukertype, brukerId, handleSubmit) => (
	<div className="bestillingsveileder">
		<BestillingsveilederContext.Provider value={options}>
			<StegVelger
				initialValues={initialValues}
				onSubmit={handleSubmit}
				brukertype={brukertype}
				brukerId={brukerId}
			/>
		</BestillingsveilederContext.Provider>
	</div>
)

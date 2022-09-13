import React, { createContext, useEffect, useState } from 'react'
import { StegVelger } from './stegVelger/StegVelger'
import { AppError } from '~/components/ui/appError/AppError'
import { BVOptions } from './options/options'

import './bestillingsveileder.less'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const BestillingsveilederContext = createContext()

export const Bestillingsveileder = ({ error, sendBestilling }) => {
	const {
		currentBruker: { brukerId, brukertype },
	} = useCurrentBruker()
	const location = useLocation()
	const navigate = useNavigate()
	const [navigateRoot, setNavigateRoot] = useState(false)
	const { gruppeId } = useParams()

	useEffect(() => {
		if (navigateRoot) {
			console.warn('Noe gikk galt med bestilling, returnerer til gruppeoversikt!')
			navigate('/')
		}
	})

	if (!location.state && !gruppeId) {
		setNavigateRoot(true)
		return null
	}

	const options = BVOptions(location.state, gruppeId)
	const handleSubmit = (values) => {
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
		<ErrorBoundary>
			<BestillingsveilederContext.Provider value={options}>
				<StegVelger
					initialValues={initialValues}
					onSubmit={handleSubmit}
					brukertype={brukertype}
					brukerId={brukerId}
				/>
			</BestillingsveilederContext.Provider>
		</ErrorBoundary>
	</div>
)

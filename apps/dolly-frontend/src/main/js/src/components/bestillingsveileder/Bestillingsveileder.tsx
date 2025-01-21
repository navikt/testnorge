import React, { useEffect, useMemo, useState } from 'react'
import { StegVelger } from './stegVelger/StegVelger'
import { AppError } from '@/components/ui/appError/AppError'
import { BVOptions } from './options/options'

import './bestillingsveileder.less'
import { useLocation, useNavigate, useParams } from 'react-router-dom'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { ShowErrorContext } from '@/components/bestillingsveileder/ShowErrorContext'

export const Bestillingsveileder = ({ error, sendBestilling }) => {
	const location = useLocation()
	const navigate = useNavigate()
	const [showError, setShowError] = useState(false)
	const [navigateRoot, setNavigateRoot] = useState(false)
	const { gruppeId } = useParams()
	const erOrganisasjon = location?.state?.opprettOrganisasjon
	const importPersoner = location?.state?.importPersoner

	const contextValue = useMemo(
		() => ({
			showError,
			setShowError,
		}),
		[showError, setShowError],
	)

	useEffect(() => {
		if (navigateRoot) {
			console.warn('Noe gikk galt med bestilling, returnerer til gruppeoversikt!')
			navigate('/')
		}
	}, [navigateRoot])

	if (!location.state) {
		location.state = {
			antall: '1',
			identtype: 'FNR',
			mal: null,
		}
	}

	if (!erOrganisasjon && !gruppeId && !importPersoner) {
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

	return (
		<div className="bestillingsveileder">
			<ErrorBoundary>
				<ShowErrorContext.Provider value={contextValue}>
					<BestillingsveilederContext.Provider value={options}>
						<StegVelger initialValues={options.initialValues} onSubmit={handleSubmit} />
					</BestillingsveilederContext.Provider>
				</ShowErrorContext.Provider>
			</ErrorBoundary>
		</div>
	)
}

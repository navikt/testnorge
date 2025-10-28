import React, { useEffect, useMemo, useState } from 'react'
import { StegVelger } from './stegVelger/StegVelger'
import { AppError } from '@/components/ui/appError/AppError'
import { BVOptions } from './options/options'

import './bestillingsveileder.less'
import { useLocation, useNavigate, useParams } from 'react-router'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { ShowErrorContext } from '@/components/bestillingsveileder/ShowErrorContext'
import { sendBestilling } from '@/ducks/bestilling'
import { useDispatch } from 'react-redux'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'

export const Bestillingsveileder = () => {
	const location = useLocation()
	const { dollyEnvironments } = useDollyEnvironments()
	const navigate = useNavigate()
	const dispatch = useDispatch()
	const [error, setError] = useState(null)
	const [showError, setShowError] = useState(false)
	const [navigateRoot, setNavigateRoot] = useState(false)
	const { gruppeId }: any = useParams()
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
			id2032: false,
			mal: null,
		}
	}

	if (!erOrganisasjon && !gruppeId && !importPersoner) {
		setNavigateRoot(true)
		return null
	}

	const options = BVOptions(location.state, gruppeId, dollyEnvironments)

	const handleSubmit = async (values) => {
		try {
			dispatch(sendBestilling(values, options, gruppeId, navigate))
		} catch (err) {
			setError(err)
		}
	}

	if (error) {
		return <AppError title="Det skjedde en feil ved bestilling" message={error?.message} />
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

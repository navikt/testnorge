import React, { useEffect, useMemo, useState } from 'react'
import { StegVelger } from './stegVelger/StegVelger'
import { AppError } from '@/components/ui/appError/AppError'
import { deriveBestillingsveilederState } from './options/deriveBestillingsveilederState'
import './bestillingsveileder.less'
import { useLocation, useNavigate, useParams } from 'react-router'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { ShowErrorContext } from '@/components/bestillingsveileder/ShowErrorContext'
import { sendBestilling } from '@/ducks/bestilling'
import { useDispatch } from 'react-redux'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'

export const Bestillingsveileder = () => {
	const location = useLocation()
	const { dollyEnvironments } = useDollyEnvironments()
	const navigate = useNavigate()
	const dispatch = useDispatch()
	const [error, setError] = useState<any>(null)
	const [showError, setShowError] = useState(false)
	const [navigateRoot, setNavigateRoot] = useState(false)
	const { gruppeId }: any = useParams()
	const erOrganisasjon = location?.state?.opprettOrganisasjon
	const importPersoner = location?.state?.importPersoner

	const contextValue = useMemo(() => ({ showError, setShowError }), [showError, setShowError])

	useEffect(() => {
		if (navigateRoot) {
			console.warn('Noe gikk galt med bestilling, returnerer til gruppeoversikt!')
			navigate('/')
		}
	}, [navigateRoot])

	if (!location.state) {
		location.state = { antall: '1', identtype: 'FNR', id2032: false, mal: null }
	}

	if (!erOrganisasjon && !gruppeId && !importPersoner) {
		setNavigateRoot(true)
		return null
	}

	const [baseConfig, setBaseConfig] = useState<any>({
		...location.state,
		gruppeId: gruppeId ? parseInt(gruppeId, 10) : (location.state?.gruppeId ?? null),
	})
	const options = useMemo(
		() => deriveBestillingsveilederState(baseConfig, dollyEnvironments),
		[baseConfig, dollyEnvironments],
	) as any
	const setIdenttype = (value: string) =>
		setBaseConfig((prev: any) => ({ ...prev, identtype: value }))
	const setGruppeId = (value: number | null) =>
		setBaseConfig((prev: any) => ({ ...prev, gruppeId: value }))
	const setMal = (mal: any | undefined) => setBaseConfig((prev: any) => ({ ...prev, mal }))
	const updateContext = (patch: Partial<BestillingsveilederContextType>) =>
		setBaseConfig((prev: any) => ({ ...prev, ...patch }))
	const providedContext: BestillingsveilederContextType = useMemo(
		() => ({
			...options,
			identtype: options.identtype,
			setIdenttype,
			updateContext,
			gruppeId: options.gruppeId,
			setGruppeId,
			setMal,
		}),
		[options, baseConfig.gruppeId],
	)

	const handleSubmit = async (values: any) => {
		try {
			;(dispatch as any)(sendBestilling(values, options, baseConfig.gruppeId, navigate) as any)
		} catch (err: any) {
			setError(err)
		}
	}

	if (error) {
		return <AppError error={error} stackTrace={''} />
	}

	return (
		<div className="bestillingsveileder">
			<ErrorBoundary>
				<ShowErrorContext.Provider value={contextValue}>
					<BestillingsveilederContext.Provider value={providedContext}>
						<StegVelger initialValues={options.initialValues} onSubmit={handleSubmit as any} />
					</BestillingsveilederContext.Provider>
				</ShowErrorContext.Provider>
			</ErrorBoundary>
		</div>
	)
}

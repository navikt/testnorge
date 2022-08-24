import React, { Suspense, useEffect, useState } from 'react'
import { Route, Routes } from 'react-router-dom'
import Header from '~/components/layout/header/Header'
import Loading from '~/components/ui/loading/Loading'
import allRoutes from '~/allRoutes'
import { VarslingerModal } from '~/components/varslinger/VarslingerModal'
import './App.less'
import { Forbedring } from '~/components/feedback/Forbedring'
import Utlogging from '~/components/utlogging'
import ToastConnector from '~/components/ui/toast/ToastConnector'
import { Breadcrumbs } from '~/components/layout/breadcrumb/Breadcrumb'
import { useBrukerProfil, useCurrentBruker } from '~/utils/hooks/useBruker'
import { useDollyEnvironments } from '~/utils/hooks/useEnvironments'
import logoutBruker from '~/components/utlogging/logoutBruker'
import { useDollyMaler } from '~/utils/hooks/useMaler'

export const App = () => {
	const [criticalError, setCriticalError] = useState(null)

	const { loading, error: userError } = useCurrentBruker()

	// Lazyloader miljøer, maler og profilData så det ligger cachet når det trengs
	const { loading: loadingEnv } = useDollyEnvironments()
	const { loading: loadingMal } = useDollyMaler()
	const { loading: loadingProfil } = useBrukerProfil()

	const dollyLoading = loading || loadingEnv || loadingMal || loadingProfil

	useEffect(() => {
		if (userError) {
			setCriticalError(userError)
		}
	}, [userError])

	function extractFeilmelding(stackTrace: string) {
		if (stackTrace?.includes('miljoer')) {
			return 'miljoe_error'
		} else if (stackTrace?.includes('current')) {
			return 'azure_error'
		} else {
			return 'unknown_error'
		}
	}

	const logout = (stackTrace: string) => {
		const feilmelding = extractFeilmelding(stackTrace)
		logoutBruker(feilmelding)
	}

	useEffect(() => {
		if (criticalError) {
			logout(criticalError.stack)
		}
	}, [criticalError])

	if (dollyLoading) {
		return <Loading label="Laster Dolly" fullpage />
	}

	return (
		<React.Fragment>
			<Utlogging />
			<VarslingerModal />
			<Header />
			<Breadcrumbs />
			<main>
				<Suspense fallback={<Loading label="Laster inn" />}>
					<Routes>
						{allRoutes.map((route, idx) =>
							route.element ? (
								<Route
									key={idx}
									path={route.path}
									element={
										// @ts-ignore
										<route.element />
									}
								/>
							) : (
								<React.Fragment />
							)
						)}
					</Routes>
				</Suspense>
			</main>
			<Forbedring />
			<ToastConnector />
		</React.Fragment>
	)
}

import React, { Suspense, useEffect, useState } from 'react'
import { Route, Routes } from 'react-router-dom'
import Header from '@/components/layout/header/Header'
import Loading from '@/components/ui/loading/Loading'
import allRoutes from '@/allRoutes'
import { VarslingerModal } from '@/components/varslinger/VarslingerModal'
import './App.less'
import { Forbedring } from '@/components/feedback/Forbedring'
import Utlogging from '@/components/utlogging'
import ToastConnector from '@/components/ui/toast/ToastConnector'
import { Breadcrumbs } from '@/components/layout/breadcrumb/Breadcrumb'
import { InfoStripe } from '@/components/infostripe/InfoStripe'
import { useBrukerProfil, useCurrentBruker } from '@/utils/hooks/useBruker'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import {
	useDollyMalerBrukerOgMalnavn,
	useDollyOrganisasjonMalerBrukerOgMalnavn,
} from '@/utils/hooks/useMaler'
import { runningCypressE2E } from '@/service/services/Request'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'

const logout = (feilmelding: string) => {
	if (!runningCypressE2E()) {
		navigateToLogin(feilmelding)
	}
}

export const App = () => {
	const [criticalError, setCriticalError] = useState(null)

	const { loading, error: userError, currentBruker } = useCurrentBruker()

	// Lazyloader miljøer, brukerens maler og profilData så det ligger cachet ved oppstart
	useDollyEnvironments()
	useBrukerProfil()
	useDollyMalerBrukerOgMalnavn(currentBruker?.brukerId)
	useDollyOrganisasjonMalerBrukerOgMalnavn(currentBruker?.brukerId)

	useEffect(() => {
		if (userError) {
			setCriticalError(userError)
		}
	}, [userError])

	useEffect(() => {
		if (criticalError && !runningCypressE2E()) {
			logout(criticalError.stack)
		}
	}, [criticalError])

	if (loading || criticalError) {
		return <Loading label="Laster Dolly" fullpage />
	}

	return (
		<React.Fragment>
			<Utlogging />
			<VarslingerModal />
			<Header />
			<Breadcrumbs />
			<InfoStripe />
			<main>
				<Suspense fallback={<Loading label="Laster inn" />}>
					<Routes>
						{allRoutes.map((route: { element: any; path: string }, idx: React.Key) =>
							route.element ? (
								<Route key={idx} path={route.path} element={<route.element />} />
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

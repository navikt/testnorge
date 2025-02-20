import React, { Suspense, useEffect, useState } from 'react'
import { Route, Routes } from 'react-router-dom'
import Header from '@/components/layout/header/Header'
import Loading from '@/components/ui/loading/Loading'
import allRoutes from '@/allRoutes'
import { VarslingerModal } from '@/components/varslinger/VarslingerModal'
import './App.less'
import { Kontaktinfo } from '@/components/feedback/Kontaktinfo'
import ToastConnector from '@/components/ui/toast/ToastConnector'
import { Breadcrumbs } from '@/components/layout/breadcrumb/Breadcrumb'
import { useBrukerProfil, useCurrentBruker } from '@/utils/hooks/useBruker'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import {
	useDollyMalerBrukerOgMalnavn,
	useDollyOrganisasjonMalerBrukerOgMalnavn,
} from '@/utils/hooks/useMaler'
import { runningE2ETest } from '@/service/services/Request'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'
import { FaroErrorBoundary } from '@grafana/faro-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { InfoStripe } from '@/components/infostripe/InfoStripe'

const logout = (feilmelding: string) => {
	if (!runningE2ETest()) {
		navigateToLogin(feilmelding)
	}
}

export const App = () => {
	const [criticalError, setCriticalError]: any = useState(null)

	const { loading, error: userError, currentBruker } = useCurrentBruker()

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
		if (criticalError && !runningE2ETest()) {
			console.error(criticalError)
			logout(criticalError?.stack)
		}
	}, [criticalError])

	if (loading || criticalError) {
		return <Loading label="Laster Dolly" fullpage />
	}

	return (
		<FaroErrorBoundary>
			<VarslingerModal />
			<Header />
			<Breadcrumbs />
			<InfoStripe />
			<main>
				<ErrorBoundary>
					<Suspense fallback={<Loading label="Laster inn" />}>
						<Routes>
							{allRoutes.map((route: { element: any; path: string }, idx: React.Key) =>
								route.element ? (
									<Route key={idx} path={route.path} element={<route.element />} />
								) : (
									<React.Fragment key={idx} />
								),
							)}
						</Routes>
					</Suspense>
				</ErrorBoundary>
			</main>
			<Kontaktinfo />
			<ToastConnector />
		</FaroErrorBoundary>
	)
}

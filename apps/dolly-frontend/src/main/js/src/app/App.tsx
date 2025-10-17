import React, { Suspense, useEffect, useState } from 'react'
import { Outlet } from 'react-router'
import Header from '@/components/layout/header/Header'
import Loading from '@/components/ui/loading/Loading'
import { VarslingerModal } from '@/components/varslinger/VarslingerModal'
import './App.less'
import { Kontaktinfo } from '@/components/feedback/Kontaktinfo'
import ToastConnector from '@/components/ui/toast/ToastConnector'
import { Breadcrumbs } from '@/components/layout/breadcrumb/Breadcrumb'
import { useBrukerProfil, useCurrentBruker } from '@/utils/hooks/useBruker'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import {
	useDollyOrganisasjonMalerBrukerOgMalnavn,
	useMalbestillingBruker,
} from '@/utils/hooks/useMaler'
import { runningE2ETest } from '@/service/services/Request'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { InfoStripe } from '@/components/infostripe/InfoStripe'
import { RouteChangeHandler } from '@/RootComponent'
import { NavigationTitle } from '@/NavigationTitle'

const logout = (feilmelding: string) => {
	console.error('Kritisk feil i Dolly, logger ut: ', feilmelding)
	if (!runningE2ETest()) {
		navigateToLogin(feilmelding)
	}
}

export const App = () => {
	const [criticalError, setCriticalError]: any = useState(null)

	const { loading, error: userError, currentBruker } = useCurrentBruker()

	useDollyEnvironments()
	useBrukerProfil()
	useMalbestillingBruker(currentBruker?.brukerId)
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
		<ErrorBoundary>
			<RouteChangeHandler />
			<VarslingerModal />
			<Header />
			<Breadcrumbs />
			<InfoStripe />
			<main>
				<ErrorBoundary>
					<Suspense fallback={<Loading label="Laster inn" />}>
						<NavigationTitle />
						<Outlet />
					</Suspense>
				</ErrorBoundary>
			</main>
			<Kontaktinfo />
			<ToastConnector />
		</ErrorBoundary>
	)
}

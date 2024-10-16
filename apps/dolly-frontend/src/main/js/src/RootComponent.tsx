import { Provider } from 'react-redux'
import {
	createRoutesFromChildren,
	matchRoutes,
	Route,
	Routes,
	useLocation,
	useNavigationType,
} from 'react-router-dom'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import BrukerPage from '@/pages/brukerPage'
import LoginPage from '@/pages/loginPage'
import { history, store } from '@/Store'
import { HistoryRouter as Router } from 'redux-first-history/rr6'
import { SWRConfig } from 'swr'
import { App } from '@/app/App'
import nais from './nais'

import {
	FaroRoutes,
	getWebInstrumentations,
	initializeFaro,
	ReactIntegration,
	ReactRouterVersion,
} from '@grafana/faro-react'
import { useRouteError } from 'react-router'
import { AppError } from '@/components/ui/appError/AppError'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'

initializeFaro({
	paused: window.location.hostname.includes('localhost'),
	url: nais.telemetryCollectorURL,
	app: nais.app,
	instrumentations: [
		...getWebInstrumentations(),

		new ReactIntegration({
			router: {
				version: ReactRouterVersion.V6,
				dependencies: {
					createRoutesFromChildren,
					matchRoutes,
					Routes,
					useLocation,
					useNavigationType,
				},
			},
		}),
	],
})

const ErrorView = () => {
	console.error('Applikasjonen har støtt på en feil')
	const error: any = useRouteError()
	console.error(error)

	const errors = [
		'Failed to fetch dynamically imported module',
		'Unable to preload CSS',
		"Cannot destructure property of 'register'",
	]

	if (errors.some((e) => error?.message?.includes(e))) {
		navigateToLogin(error?.message)
	}
	return <AppError error={error} stackTrace={error.stackTrace} />
}

export const RootComponent = () => (
	<ErrorBoundary>
		<Provider store={store}>
			<Router history={history}>
				<SWRConfig
					value={{
						dedupingInterval: 5000,
						revalidateOnFocus: false,
					}}
				>
					<FaroRoutes>
						<Route path="/login" element={<LoginPage />} />
						<Route errorElement={<ErrorView />} path="/bruker" element={<BrukerPage />} />
						<Route errorElement={<ErrorView />} path="*" element={<App />} />
					</FaroRoutes>
				</SWRConfig>
			</Router>
		</Provider>
	</ErrorBoundary>
)

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

export const RootComponent = () => (
	<Provider store={store}>
		<Router history={history}>
			<ErrorBoundary>
				<SWRConfig
					value={{
						dedupingInterval: 5000,
						revalidateOnFocus: false,
					}}
				>
					<FaroRoutes>
						<Route path="/bruker" element={<BrukerPage />} />
						<Route path="/login" element={<LoginPage />} />
						<Route path="*" element={<App />} />
					</FaroRoutes>
				</SWRConfig>
			</ErrorBoundary>
		</Router>
	</Provider>
)

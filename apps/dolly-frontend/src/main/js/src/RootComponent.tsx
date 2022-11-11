import React from 'react'
import { Provider } from 'react-redux'
import { Route, Routes } from 'react-router-dom'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import BrukerPage from '~/pages/brukerPage'
import LoginPage from '~/pages/loginPage'
import { history, store } from '~/Store'
import { HistoryRouter as Router } from 'redux-first-history/rr6'
import { SWRConfig } from 'swr'
import { App } from '~/app/App'
import StatusPage from "~/pages/statusPage";

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
					<Routes>
						<Route path="/bruker" element={<BrukerPage />} />
						<Route path="/login" element={<LoginPage />} />
						<Route path="/status" element={<StatusPage />} />
						<Route path="*" element={<App />} />
					</Routes>
				</SWRConfig>
			</ErrorBoundary>
		</Router>
	</Provider>
)

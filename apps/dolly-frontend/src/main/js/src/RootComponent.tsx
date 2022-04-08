import React from 'react'
import { Provider } from 'react-redux'
import { Route, Routes } from 'react-router-dom'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import BrukerPage from '~/pages/brukerPage'
import LoginPage from '~/pages/loginPage'
import AppConnector from '~/app/AppConnector'
import { history, store } from '~/Store'
import { HistoryRouter as Router } from 'redux-first-history/rr6'

export const RootComponent = () => (
	<Provider store={store}>
		<Router history={history}>
			<ErrorBoundary>
				<Routes>
					<Route path="/bruker" element={<BrukerPage />} />
					<Route path="/login" element={<LoginPage />} />
					<Route path="*" element={<AppConnector />} />
				</Routes>
			</ErrorBoundary>
		</Router>
	</Provider>
)

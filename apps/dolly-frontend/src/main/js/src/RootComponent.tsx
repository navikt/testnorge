import React from 'react'
import { Provider } from 'react-redux'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import BrukerPage from '~/pages/brukerPage'
import LoginPage from '~/pages/loginPage'
import AppConnector from '~/app/AppConnector'
import { configureReduxStore } from '~/Store'

export const RootComponent = () => {
	return (
		<Provider store={configureReduxStore()}>
			<BrowserRouter>
				<ErrorBoundary>
					<Routes>
						<Route path="/bruker" element={<BrukerPage />} />
						<Route path="/login" element={<LoginPage />} />
						<Route path="*" element={<AppConnector />} />
					</Routes>
				</ErrorBoundary>
			</BrowserRouter>
		</Provider>
	)
}

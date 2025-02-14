import { Provider } from 'react-redux'
import {
	createBrowserRouter,
	createRoutesFromElements,
	Route,
	RouterProvider,
	useRouteError,
} from 'react-router'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import BrukerPage from '@/pages/brukerPage'
import LoginPage from '@/pages/loginPage'
import { store } from '@/Store'
import { SWRConfig } from 'swr'
import { App } from '@/app/App'
import { AppError } from '@/components/ui/appError/AppError'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'
import allRoutes from '@/allRoutes'
import React from 'react'

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

const router = createBrowserRouter(
	createRoutesFromElements(
		<>
			<Route path="/login" element={<LoginPage />} />
			<Route path="/bruker" element={<BrukerPage />} errorElement={<ErrorView />} />
			<Route
				path="/"
				element={<App />}
				handle={{ crumb: () => 'Hjem' }}
				errorElement={<ErrorView />}
			>
				{allRoutes.map((route, idx) => (
					<Route key={idx} path={route.path} handle={route.handle} element={<route.element />} />
				))}
			</Route>
		</>,
	),
)

export const RootComponent = () => (
	<ErrorBoundary>
		<Provider store={store}>
			<SWRConfig
				value={{
					dedupingInterval: 5000,
					revalidateOnFocus: false,
				}}
			>
				<RouterProvider router={router} />
			</SWRConfig>
		</Provider>
	</ErrorBoundary>
)

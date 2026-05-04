import { Provider, useDispatch } from 'react-redux'
import {
	createBrowserRouter,
	createRoutesFromElements,
	Route,
	RouterProvider,
	useLocation,
	useRouteError,
} from 'react-router'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import LoginPage from '@/pages/loginPage'
import { store } from '@/Store'
import { SWRConfig } from 'swr'
import { App } from '@/app/App'
import { AppError } from '@/components/ui/appError/AppError'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'
import allRoutes from '@/allRoutes'
import React, { useEffect } from 'react'
import { locationChange } from '@/ducks/finnPerson'
import BrukerPage from '@/pages/brukerPage'
import { handleChunkErrorWithReload, isChunkLoadError } from '@/utils/chunkErrorUtils'

const ErrorView = () => {
	console.error('Applikasjonen har støtt på en feil')
	const error: any = useRouteError()
	console.error(error)

	const isMinifiedReactError = error?.message?.includes('Minified React error')

	if (isMinifiedReactError) {
		console.error('🔴 MINIFIED REACT ERROR DETECTED 🔴')
		console.error('Error message:', error?.message)
		console.error('Error name:', error?.name)
		console.error('Error stack:', error?.stack)
		console.error('Current URL:', window.location.href)
		console.error('Current pathname:', window.location.pathname)
		console.error('Session storage:', {
			keys: Object.keys(sessionStorage),
			values: Object.fromEntries(
				Object.entries(sessionStorage).map(([k, v]) => [k, v.substring(0, 100)]),
			),
		})
	}

	if (isChunkLoadError(error)) {
		const didReload = handleChunkErrorWithReload()
		if (didReload) return null
	}

	const authErrors = ["Cannot destructure property of 'register'"]

	if (authErrors.some((e) => error?.message?.includes(e))) {
		console.error('Navigating to login due to auth-related error')
		navigateToLogin(error?.message)
	}
	return <AppError error={error} stackTrace={error.stackTrace} />
}

export function RouteChangeHandler() {
	const location = useLocation()
	const dispatch = useDispatch()

	useEffect(() => {
		dispatch(locationChange(location))
	}, [location, dispatch])

	return null
}

const router = createBrowserRouter(
	createRoutesFromElements(
		<>
			<Route path="/login" element={<LoginPage />} errorElement={<ErrorView />} />
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
					errorRetryCount: 3,
					shouldRetryOnError: (error) => {
						return !(error?.status === 404 || error?.response?.status === 404)
					},
				}}
			>
				<RouterProvider router={router} />
			</SWRConfig>
		</Provider>
	</ErrorBoundary>
)

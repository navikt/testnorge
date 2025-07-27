import { useEffect } from 'react'
import { matchPath, useLocation } from 'react-router'
import allRoutes from '@/allRoutes'

export const NavigationTitle = () => {
	const location = useLocation()

	useEffect(() => {
		const path = location.pathname

		const route = allRoutes.find((r) => {
			const routePath = r.path.replace(/\/:[^/]+/g, '/[^/]+')
			const regex = new RegExp(`^${routePath}$`.replace(/\//g, '\\/'))
			return regex.test(path)
		})

		if (route?.handle?.crumb) {
			const match = matchPath(route.path, path)
			const pathParams = match?.params || {}

			const searchParams = Object.fromEntries(new URLSearchParams(location.search))

			const params = { ...pathParams, ...searchParams }

			// Gruppe må håndteres spesielt, fordi den har en crumb som mottar props
			if (route.path.includes('/gruppe/:gruppeId')) {
				document.title = `Dolly - Gruppe ${pathParams.gruppeId}`
			} else {
				const crumbValue =
					typeof route.handle.crumb === 'function'
						? route.handle.crumb({ params })
						: route.handle.crumb

				document.title = `Dolly - ${String(crumbValue)}`
			}
		} else {
			document.title = 'Dolly'
		}
	}, [location])

	return null
}

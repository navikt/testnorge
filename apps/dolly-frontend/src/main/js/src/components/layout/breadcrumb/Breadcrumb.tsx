import { NavLink, useLocation } from 'react-router'
import cn from 'classnames'
import Version from '@/components/version/Version'
import allRoutes from '@/allRoutes'

import './Breadcrumb.less'

interface Crumb {
	pathname: string
	label: string | JSX.Element
}

export const Breadcrumbs = () => {
	const location = useLocation()
	const pathnames = location.pathname.split('/').filter((x) => x)

	const crumbs: Crumb[] = [
		{ pathname: '/', label: 'Hjem' },
		...pathnames.map((_, index) => {
			const pathname = `/${pathnames.slice(0, index + 1).join('/')}`
			const route = allRoutes.find((route) => {
				const routePath = route.path.replace(/:\w+/g, '[^/]+')
				return new RegExp(`^${routePath}$`).test(pathname)
			})
			const params = { gruppeId: pathnames[1], personId: pathnames[3] }
			const label = route?.handle?.crumb({ params }) || ''

			return { pathname, label }
		}),
	]

	return (
		<nav aria-label="breadcrumb" className="breadcrumb">
			<ol>
				{crumbs.map((crumb, idx) => {
					const active = crumb.pathname === location.pathname
					const classes = cn('breadcrumb-item', {})

					return (
						<li className={classes} key={idx}>
							{active ? crumb.label : <NavLink to={crumb.pathname}>{crumb.label}</NavLink>}
						</li>
					)
				})}
			</ol>
			<Version />
		</nav>
	)
}

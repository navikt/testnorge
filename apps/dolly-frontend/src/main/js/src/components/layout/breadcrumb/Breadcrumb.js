import React from 'react'
import { NavLink, useLocation } from 'react-router-dom'
import cn from 'classnames'
import Version from '~/components/version/Version'

import './Breadcrumb.less'
import allRoutes from '~/Routes'
import useReactRouterBreadcrumbs from 'use-react-router-breadcrumbs'

export const Breadcrumbs = () => {
	const location = useLocation()

	const isActive = (match, location) => match.pathname === location.pathname

	const breadcrumbs = useReactRouterBreadcrumbs(allRoutes)
	return (
		<nav aria-label="breadcrumb" className="breadcrumb">
			<ol>
				{breadcrumbs.map(({ match, breadcrumb }, idx) => {
					const active = isActive(match, location)
					const classes = cn('breadcrumb-item', {})

					return (
						<li className={classes} key={idx}>
							{active ? breadcrumb : <NavLink to={match.pathname}>{breadcrumb}</NavLink>}
						</li>
					)
				})}
			</ol>
			<Version />
		</nav>
	)
}

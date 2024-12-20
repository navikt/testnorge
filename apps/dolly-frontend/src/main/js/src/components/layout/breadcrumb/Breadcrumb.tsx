import { NavLink, useLocation, useMatches } from 'react-router'
import cn from 'classnames'
import Version from '@/components/version/Version'

import './Breadcrumb.less'

export const Breadcrumbs = () => {
	const location = useLocation()
	let matches = useMatches()

	let crumbs = matches
		.filter((match) => Boolean(match.handle?.crumb))
		.map((match) => match.handle.crumb(match.data))

	const isActive = (match, currentLocation) => match.pathname === currentLocation.pathname
	//TODO: Må fikses

	return (
		<nav aria-label="breadcrumb" className="breadcrumb">
			<ol>
				{crumbs.map(({ match, breadcrumb }, idx) => {
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

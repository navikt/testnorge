import { NavLink, useLocation, useMatches } from 'react-router'
import cn from 'classnames'
import Version from '@/components/version/Version'

import './Breadcrumb.less'

export const Breadcrumbs = () => {
	const location = useLocation()
	let matches = useMatches()

	let crumbs = matches.filter((match) => Boolean(match.handle?.crumb))

	const isActive = (match, currentLocation) => match.pathname === currentLocation.pathname

	return (
		<nav aria-label="breadcrumb" className="breadcrumb">
			<ol>
				{crumbs.map((match, idx) => {
					const active = isActive(match, location)
					const classes = cn('breadcrumb-item', {})

					return (
						<li className={classes} key={idx}>
							{active ? (
								match?.handle?.crumb(match)
							) : (
								<NavLink to={match.pathname}>{match?.handle?.crumb(match)}</NavLink>
							)}
						</li>
					)
				})}
			</ol>
			<Version />
		</nav>
	)
}

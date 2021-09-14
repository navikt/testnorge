import React from 'react'
import { NavLink } from 'react-router-dom'
import cn from 'classnames'
import Version from '~/components/version/Version'

import './Breadcrumb.less'

const isActive = (match, location) => match.url === location.pathname

export const Breadcrumbs = ({ breadcrumbs }) => (
	<nav aria-label="breadcrumb" className="breadcrumb">
		<ol>
			{breadcrumbs.map(({ match, location, breadcrumb }) => {
				const active = isActive(match, location)
				const classes = cn('breadcrumb-item', {
					active,
				})

				return (
					<li className={classes} key={breadcrumb.key}>
						{active ? breadcrumb : <NavLink to={match.url}>{breadcrumb}</NavLink>}
					</li>
				)
			})}
		</ol>
		<Version />
	</nav>
)

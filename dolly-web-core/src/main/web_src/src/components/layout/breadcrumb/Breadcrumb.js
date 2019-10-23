import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { NavLink } from 'react-router-dom'
import cn from 'classnames'
import Version from '~/components/version/Version'

import './Breadcrumb.less'

export default class Breadcrumb extends PureComponent {
	isActive = bc => bc.props.match.url === bc.props.location.pathname

	render() {
		const { breadcrumbs } = this.props

		if (
			breadcrumbs[0].key === '/' &&
			breadcrumbs.length > 1 &&
			breadcrumbs[1].key === '/tpsendring'
		) {
			breadcrumbs[0] = null
		}

		return (
			<nav aria-label="breadcrumb" className="breadcrumb">
				<ol>
					{breadcrumbs.map((breadcrumb, index) => {
						if (breadcrumb) {
							const active = this.isActive(breadcrumb)
							const classes = cn('breadcrumb-item', {
								active
							})

							const crumb = active ? (
								breadcrumb
							) : (
								<NavLink to={breadcrumb.props.match.url}>{breadcrumb}</NavLink>
							)

							return (
								<li className={classes} key={breadcrumb.key}>
									{crumb}
								</li>
							)
						}
					})}
				</ol>
				<Version />
			</nav>
		)
	}
}

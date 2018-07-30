import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { NavLink } from 'react-router-dom'
import cn from 'classnames'
import withBreadcrumbs from 'react-router-breadcrumbs-hoc'
import routes from '~/Routes'

import './Breadcrumb.less'

class Breadcrumbs extends PureComponent {
	isActive = bc => bc.props.match.url === bc.props.location.pathname

	render() {
		const { breadcrumbs } = this.props

		console.log('test')
		return (
			<nav aria-label="breadcrumb" className="breadcrumb">
				<ol>
					{breadcrumbs.map((breadcrumb, index) => {
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
					})}
				</ol>
			</nav>
		)
	}
}

export default withBreadcrumbs(routes, { disableDefaults: true })(Breadcrumbs)

import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { NavLink } from 'react-router-dom'
import cn from 'classnames'
import Version from '~/components/version/Version'

import './Breadcrumb.less'

class Breadcrumb extends PureComponent {
	isActive = bc => bc.props.match.url === bc.props.location.pathname

	render() {
		const { breadcrumbs } = this.props
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
				<Version />
			</nav>
		)
	}
}

export default Breadcrumb

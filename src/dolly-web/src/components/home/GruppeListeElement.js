import React from 'react'
import { NavLink } from 'react-router-dom'

export default ({ gruppeElement }) => {
	const pathToGroup = '/gruppe/' + gruppeElement.id
	return (
		<div>
			<NavLink to={pathToGroup} activeClassName="">
				Gruppe: --> {gruppeElement.navn}
			</NavLink>
		</div>
	)
}

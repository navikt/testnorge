import React from 'react'
import Knapp from 'nav-frontend-knapper'

const NavButton = ({ type = 'standard', children, ...restProps }) => {
	return (
		<Knapp type={type} {...restProps}>
			{children}
		</Knapp>
	)
}

export default NavButton

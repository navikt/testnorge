import React from 'react'
import Knapp, { KnappBaseProps } from 'nav-frontend-knapper'

const NavButton = ({ type = 'standard', children, ...restProps }: KnappBaseProps) => (
	<Knapp type={type} {...restProps}>
		{children}
	</Knapp>
)

export default NavButton

import React from 'react'
import Knapp from 'nav-frontend-knapper'
import styled from 'styled-components'

const StyledNavKnapp = styled(Knapp)`
	&.knapp--standard {
		transform: translateY(-2px);
	}
`

const NavButton = ({ type = 'standard', children, ...restProps }) => (
	<StyledNavKnapp type={type} {...restProps}>
		{children}
	</StyledNavKnapp>
)

export default NavButton

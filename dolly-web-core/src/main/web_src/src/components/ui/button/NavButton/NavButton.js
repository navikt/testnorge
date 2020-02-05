import React from 'react'
import Knapp from 'nav-frontend-knapper'

const NavButton = ({ direction, ...restProps }) => {
	return (
		<Knapp type={direction == 'forward' ? 'hoved' : 'standard'} {...restProps}>
			<div className="nav-button-container">
				{direction === 'forward' && 'Videre'}
				{direction === 'backward' && ' Tilbake'}
			</div>
		</Knapp>
	)
}

export default NavButton

import React from 'react'
import Button from '../Button'
import Icon from '~/components/icon/Icon'

import './NavButton.less'

const NavButton = ({ direction, ...restProps }) => {
	//TODO: Use chevron icons with proper outline
	return (
		<Button {...restProps}>
			<div className="nav-button-container">
				{direction === 'forward' && 'Videre'}
				<Icon kind={`arrow-circle-${direction === 'forward' ? 'right' : 'left'}`} />
				{direction === 'backward' && ' Tilbake'}
			</div>
		</Button>
	)
}

export default NavButton

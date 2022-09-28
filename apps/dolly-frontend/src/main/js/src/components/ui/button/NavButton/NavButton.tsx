import React from 'react'
import { Button, ButtonProps } from '@navikt/ds-react'

const NavButton = ({ variant = 'primary', children, ...restProps }: ButtonProps) => (
	<Button variant={variant} {...restProps}>
		{children}
	</Button>
)

export default NavButton

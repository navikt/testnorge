import { Button, ButtonProps } from '@navikt/ds-react'

const NavButton = ({
	variant = 'primary',
	type = 'submit',
	children,
	...restProps
}: ButtonProps) => (
	<Button variant={variant} {...restProps} type={type}>
		{children}
	</Button>
)

export default NavButton

import { Button, ButtonProps } from '@navikt/ds-react'

const NavButton = ({ variant = 'primary', children, ...restProps }: ButtonProps) => (
	<Button variant={variant} {...restProps} type={'submit'}>
		{children}
	</Button>
)

export default NavButton

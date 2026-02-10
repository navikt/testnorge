import { Alert, AlertProps } from '@navikt/ds-react'
import styled from 'styled-components'

const StyledAlert = styled(Alert)`
	margin-bottom: 20px;

	.aksel-alert__wrapper {
		hyphens: auto;
		max-width: 100rem;
	}
`

export default (props: AlertProps) => <StyledAlert {...props} />

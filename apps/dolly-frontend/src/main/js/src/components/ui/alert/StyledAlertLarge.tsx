import { Alert } from '@navikt/ds-react'
import styled from 'styled-components'

const StyledAlertLarge = styled(Alert)`
	margin-bottom: 20px;

	.navds-alert__wrapper {
		max-width: 100rem;
	}
`

export default (props: any) => <StyledAlertLarge {...props} />

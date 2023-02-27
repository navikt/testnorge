import styled from 'styled-components'
import { Textarea } from '@navikt/ds-react'

export const Fritekstfelt = styled(Textarea)`
	width: 100%;
	margin-bottom: 1rem;
	margin-right: 20px;

	textarea {
		font-size: 1em;

		//:focus {
		//	outline: 0.5px solid #0067c5;
		//}
	}

	&& {
		label {
			font-size: 0.75em;
			text-transform: uppercase;
			font-weight: 400;
			margin-bottom: -8px;
		}
	}
`

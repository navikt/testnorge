import styled from 'styled-components'
import { Textarea } from '@navikt/ds-react'

export const Fritekstfelt = styled(Textarea)`
	width: 100%;
	margin-bottom: 1rem;
	margin-right: 20px;

	textarea {
		font-size: 1em;
	}

	p {
		font-size: 1em;
		font-weight: normal;
		font-style: italic;

		&::before {
			content: none !important;
		}
	}

	&& {
		label {
			font-size: 0.75em;
			text-transform: uppercase;
			font-weight: 400;
		}
	}
`

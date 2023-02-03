import { Alert } from '@navikt/ds-react'
import styled from 'styled-components'

const StyledAlert = styled(Alert)`
	margin-bottom: 20px;
	&&& {
		.navds-alert__wrapper {
			max-width: 60rem;
		}
	}
`

export const AlertAaregRequired = ({ meldingSkjema }: { meldingSkjema: string }) => {
	return (
		<StyledAlert variant={'warning'} size={'small'}>
			Personen må ha et arbeidsforhold knyttet til den samme virksomheten som du velger i{' '}
			{meldingSkjema}. Det kan du legge til ved å gå tilbake til forrige side og huke av for
			Arbeidsforhold (Aareg) under Arbeid og inntekt.
		</StyledAlert>
	)
}

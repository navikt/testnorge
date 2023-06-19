import { Alert } from '@navikt/ds-react'
import styled from 'styled-components'

const StyledAlertSmall = styled(Alert)`
	margin-bottom: 20px;
	&&& {
		.navds-alert__wrapper {
			max-width: 60rem;
		}
	}
`

export default ({ meldingSkjema }: { meldingSkjema: string }) => {
	return (
		<StyledAlertSmall variant={'warning'} size={'small'}>
			Personen må ha et arbeidsforhold knyttet til den samme virksomheten som du velger i{' '}
			{meldingSkjema}. Det kan du legge til ved å gå tilbake til forrige side og huke av for
			Arbeidsforhold (Aareg) under Arbeid og inntekt.
		</StyledAlertSmall>
	)
}

import React from 'react'
import { Alert } from '@navikt/ds-react'

export const AlertAaregRequired = ({ meldingSkjema }: { meldingSkjema: string }) => {
	return (
		<Alert variant={'warning'} style={{ marginBottom: '20px' }}>
			Personen må ha et arbeidsforhold knyttet til den samme virksomheten som du velger i{' '}
			{meldingSkjema}. Det kan du legge til ved å gå tilbake til forrige side og huke av for
			Arbeidsforhold (Aareg) under Arbeid og inntekt.
		</Alert>
	)
}

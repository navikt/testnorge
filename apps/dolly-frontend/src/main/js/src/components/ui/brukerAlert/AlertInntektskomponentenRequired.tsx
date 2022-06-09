import React from 'react'
import { Alert } from '@navikt/ds-react'

export const AlertInntektskomponentenRequired = ({ vedtak }: { vedtak: string }) => {
	return (
		<Alert variant={'warning'} style={{ marginBottom: '20px' }}>
			Personen må ha gyldig inntekt i A-ordningen for å kunne sette {vedtak}. Det kan du legge til
			ved å gå tilbake til forrige side og huke av for A-ordningen (Inntektstub) under Arbeid og
			inntekt. For lettere utfylling anbefales bruk av forenklet versjon.
		</Alert>
	)
}

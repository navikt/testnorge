import { InfoCard } from '@navikt/ds-react'
import { ExclamationmarkTriangleFillIcon } from '@navikt/aksel-icons'

export const AlertInntektskomponentenRequired = ({ vedtak }: { vedtak: string }) => {
	return (
		<div className="flexbox--full-width">
			<InfoCard data-color="warning" size={'small'} style={{ marginBottom: '20px' }}>
				<InfoCard.Message icon={<ExclamationmarkTriangleFillIcon aria-hidden />}>
					Personen må ha gyldig inntekt i minimum 12 måneder i A-ordningen for å kunne sette{' '}
					{vedtak}. Det kan du legge til ved å gå tilbake til forrige side og huke av for
					A-ordningen (Inntektstub) under Arbeid og inntekt. For lettere utfylling anbefales bruk av
					forenklet versjon.
				</InfoCard.Message>
			</InfoCard>
		</div>
	)
}

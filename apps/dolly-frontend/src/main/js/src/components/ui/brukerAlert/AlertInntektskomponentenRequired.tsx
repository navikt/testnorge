import StyledAlert from '@/components/ui/alert/StyledAlert'

export const AlertInntektskomponentenRequired = ({ vedtak }: { vedtak: string }) => {
	return (
		<div className="flexbox--full-width">
			<StyledAlert variant={'warning'} size={'small'}>
				Personen må ha gyldig inntekt i A-ordningen for å kunne sette {vedtak}. Det kan du legge til
				ved å gå tilbake til forrige side og huke av for A-ordningen (Inntektstub) under Arbeid og
				inntekt. For lettere utfylling anbefales bruk av forenklet versjon.
			</StyledAlert>
		</div>
	)
}

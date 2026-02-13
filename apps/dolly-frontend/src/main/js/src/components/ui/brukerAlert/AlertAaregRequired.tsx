import StyledAlert from '@/components/ui/alert/StyledAlert'

export const AlertAaregRequired = ({ meldingSkjema }: { meldingSkjema: string }) => {
	return (
		<StyledAlert variant={'warning'} size={'small'}>
			Personen må ha et arbeidsforhold knyttet til den samme virksomheten som du velger i{' '}
			{meldingSkjema}. Det kan du legge til ved å gå tilbake til forrige side og huke av for
			Arbeidsforhold (Aareg) under Arbeid og inntekt.
		</StyledAlert>
	)
}

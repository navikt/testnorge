import { InfoCard } from '@navikt/ds-react'
import { ExclamationmarkTriangleFillIcon } from '@navikt/aksel-icons'

export const AlertAaregRequired = ({ meldingSkjema }: { meldingSkjema: string }) => {
	return (
		<InfoCard data-color="warning" size={'small'} style={{ marginBottom: '20px' }}>
			<InfoCard.Message icon={<ExclamationmarkTriangleFillIcon aria-hidden />}>
				Personen må ha et arbeidsforhold knyttet til den samme virksomheten som du velger i{' '}
				{meldingSkjema}. Det kan du legge til ved å gå tilbake til forrige side og huke av for
				Arbeidsforhold (Aareg) under Arbeid og inntekt.
			</InfoCard.Message>
		</InfoCard>
	)
}

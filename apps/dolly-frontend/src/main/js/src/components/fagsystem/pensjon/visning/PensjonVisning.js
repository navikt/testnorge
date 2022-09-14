import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Panel from '~/components/ui/panel/Panel'
import { AlertStripeAdvarsel } from 'nav-frontend-alertstriper'

export const PensjonVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster pensjonforvalter-data" />
	if (!data) return null

	const manglerFagsystemdata = !data?.inntekter || data?.inntekter?.length === 0
	// if (!data?.inntekter || data.inntekter.length === 0) return false

	const inntektsaar = data?.inntekter?.map((inntekt) => inntekt.inntektAar)
	const foerste = Math.min(...inntektsaar)
	const siste = Math.max(...inntektsaar)

	return (
		<ErrorBoundary>
			<SubOverskrift
				label="Pensjonsgivende inntekt (POPP)"
				iconKind="pensjon"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<AlertStripeAdvarsel form="inline" style={{ marginBottom: '20px' }}>
					Kunne ikke hente arbeidsforhold-data på person
				</AlertStripeAdvarsel>
			) : (
				<Panel heading={`Pensjonsgivende inntekter (${foerste} - ${siste})`}>
					<DollyFieldArray data={data.inntekter} nested>
						{(inntekt, idx) => (
							<div className="person-visning_content" key={idx}>
								<TitleValue title="Inntektsår" value={inntekt.inntektAar} />
								<TitleValue title="Beløp" value={inntekt.belop} />
							</div>
						)}
					</DollyFieldArray>
				</Panel>
			)}
		</ErrorBoundary>
	)
}

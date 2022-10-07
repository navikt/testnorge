import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Panel from '~/components/ui/panel/Panel'
import { runningTestcafe } from '~/service/services/Request'

export const PensjonVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster pensjonforvalter-data" />
	if (!data?.inntekter || data.inntekter.length === 0) return null

	const inntektsaar = data.inntekter.map((inntekt) => inntekt.inntektAar)
	const foerste = Math.min(...inntektsaar)
	const siste = Math.max(...inntektsaar)

	return (
		<ErrorBoundary>
			<SubOverskrift label="Pensjonsgivende inntekt (POPP)" iconKind="pensjon" />

			<Panel
				startOpen={runningTestcafe()}
				heading={`Pensjonsgivende inntekter (${foerste} - ${siste})`}
			>
				<DollyFieldArray data={data.inntekter} nested>
					{(inntekt, idx) => (
						<div className="person-visning_content" key={idx}>
							<TitleValue title="Inntektsår" value={inntekt.inntektAar} />
							<TitleValue title="Beløp" value={inntekt.belop} />
						</div>
					)}
				</DollyFieldArray>
			</Panel>
		</ErrorBoundary>
	)
}

import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Panel from '~/components/ui/panel/Panel'

export const PensjonVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster pensjonforvalter-data" />
	if (!data || data.length === 0) return false

	return (
		<ErrorBoundary>
			<SubOverskrift label="Pensjonsgivende inntekt (POPP)" iconKind="pensjon" />

			<Panel heading="Pensjonsgivende inntekter">
				{' '}
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

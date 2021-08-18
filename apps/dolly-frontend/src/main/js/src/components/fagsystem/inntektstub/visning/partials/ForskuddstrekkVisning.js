import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { InntektstubKodeverk } from '~/config/kodeverk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const ForskuddstrekkVisning = ({ data }) => {
	if (!data || data.length === 0) return null

	return (
		<React.Fragment>
			<h4>Forskuddstrekk</h4>
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(id, idx) => (
						<div className="person-visning_content" key={idx}>
							<TitleValue title="Beløp" value={id.beloep} />
							<TitleValue
								title="Beskrivelse"
								value={id.beskrivelse}
								kodeverk={InntektstubKodeverk.Forskuddstrekkbeskrivelse}
							/>
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</React.Fragment>
	)
}

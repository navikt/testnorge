import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { InntektstubKodeverk } from '~/config/kodeverk'

interface ArbforholdVisning {
	data: any
}
export default ({ data }: ArbforholdVisning) => {
	if (!data || data.length === 0) return null

	return (
		<React.Fragment>
			<h4>Arbeidsforhold</h4>
			<TitleValue title="Beløp" value={data.beregnetInntekt.beloep} />
			{/* <TitleValue title="Første fraværsdag" value={data.beregnetInntekt.foersteFravaersdag} /> */}
			{/* <TitleValue title="Årsak ved endring" value={data.beregnetInntekt.aarsakVedEndring} /> */}
			{/* <DollyFieldArray data={data} header="Avtalt ferie" nested>
				{(id: any, idx: number) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Beløp" value={id.beloep} />
						<TitleValue
							title="Beskrivelse"
							value={id.beskrivelse}
							kodeverk={InntektstubKodeverk.Fradragbeskrivelse}
						/>
					</div>
				)}
			</DollyFieldArray> */}
		</React.Fragment>
	)
}

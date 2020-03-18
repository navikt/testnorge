import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const AltinnInntektVisning = ({ bestilling, loading }) => {
	// export const AltinnInntektVisning = ({ data, loading }) => {
	// if (loading) return <Loading label="laster altinn inntekt data" />
	// if (!data) return false

	// Midlertidig løsning der vi viser bestilte verdier. Data hentes egentlig som prop.
	// Husk å fjerne hent av bestilling i PersonVisning.js også

	if (!bestilling.altinnInntekt) return false
	const data = bestilling.altinnInntekt
	return (
		<div>
			<SubOverskrift label="Altinn inntekt" iconKind="altinnInntekt" />
			<DollyFieldArray data={data.inntekter} nested>
				{(inntekt, idx) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Beløp" value={inntekt.beloep} />
						<TitleValue
							title="Innsendingstidspunkt"
							value={Formatters.formatStringDates(inntekt.dato)}
						/>
						<TitleValue title="Virksomhet" value={inntekt.virksomhetsnummer} />
					</div>
				)}
			</DollyFieldArray>
		</div>
	)
}

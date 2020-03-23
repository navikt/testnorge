import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { ArbeidKodeverk } from '~/config/kodeverk'

export const Arbeidsavtaler = ({ data }) => {
	if (!data || data.length === 0) return false

	return (
		<React.Fragment>
			<h4>Arbeidsavtaler</h4>
			<DollyFieldArray data={data} nested>
				{(id, idx) => (
					<div key={idx} className="person-visning_content">
						<TitleValue title="Antall timer per uke" value={id.beregnetAntallTimerPrUke} />
						<TitleValue
							title="Arbeidstidsordning"
							value={id.arbeidstidsordning}
							kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
						/>
						<TitleValue
							title="stillingsprosent siste endringsdato"
							value={Formatters.formatStringDates(id.sistStillingsendring)}
						/>
						<TitleValue title="Stillingsprosent" value={id.stillingsprosent} />
						<TitleValue title="Yrke" value={id.yrke} kodeverk={ArbeidKodeverk.Yrker} />
					</div>
				)}
			</DollyFieldArray>
		</React.Fragment>
	)
}

import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Arbeidsavtaler = ({ data }) => {
	if (!data || data.length === 0) return false

	return (
		<div>
			<h4>Arbeidsavtaler</h4>
			<div>
				{data.map((id, idx) => (
					<div key={idx} className="person-visning_content">
						<TitleValue title="Antall timer per uke" value={id.antallTimerPrUke} />
						<TitleValue
							title="Arbeidstidsordning"
							value={id.arbeidstidsordning}
							kodeverk="Arbeidstidsordninger"
						/>
						<TitleValue title="Beregnet antall timer per uke" value={id.beregnetAntallTimerPrUke} />
						<TitleValue
							title="stillingsprosent siste endringsdato"
							value={Formatters.formatStringDates(id.sistStillingsendring)}
						/>
						<TitleValue title="Stillingsprosent" value={id.stillingsprosent} />
						<TitleValue title="Yrke" value={id.yrke} kodeverk="Yrker" />
					</div>
				))}
			</div>
		</div>
	)
}

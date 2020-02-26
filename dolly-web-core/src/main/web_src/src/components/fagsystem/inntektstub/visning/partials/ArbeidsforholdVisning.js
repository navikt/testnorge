import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const ArbeidsforholdVisning = ({ data }) => {
	if (!data || data.length === 0) return false

	return (
		<React.Fragment>
			<h4>Arbeidsforhold</h4>
			<DollyFieldArray data={data} nested>
				{(id, idx) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue
							title="Arbeidsforholdstype"
							value={id.arbeidsforholdstype}
							kodeverk="Arbeidsforholdstyper"
						/>
						<TitleValue title="Startdato" value={Formatters.formatStringDates(id.startdato)} />
						<TitleValue title="Sluttdato" value={Formatters.formatStringDates(id.sluttdato)} />
						<TitleValue
							title="Timer per uke (full stilling)"
							value={id.antallTimerPerUkeSomEnFullStillingTilsvarer}
						/>
						<TitleValue
							title="Avlønningstype"
							value={id.avloenningstype}
							kodeverk="Avlønningstyper"
						/>
						<TitleValue title="Yrke" value={id.yrke} kodeverk="Yrker" />
						<TitleValue
							title="Arbeidstidsordning"
							value={id.arbeidstidsordning}
							kodeverk="Arbeidstidsordninger"
						/>
						<TitleValue title="Stillingsprosent" value={id.stillingsprosent} />
						<TitleValue
							title="Siste lønnsendringsdato"
							value={Formatters.formatStringDates(id.sisteLoennsendringsdato)}
						/>
						<TitleValue
							title="Stillingsprosentendring"
							value={Formatters.formatStringDates(id.sisteDatoForStillingsprosentendring)}
						/>
					</div>
				)}
			</DollyFieldArray>
		</React.Fragment>
	)
}

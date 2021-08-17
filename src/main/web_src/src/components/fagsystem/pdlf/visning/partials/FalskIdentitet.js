import React, { Fragment } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { Personnavn } from './Personnavn'
import Formatters from '~/utils/DataFormatter'

export const FalskIdentitet = ({ data }) => {
	if (!data) return false
	const {
		rettIdentitetVedOpplysninger,
		rettIdentitetErUkjent,
		rettIdentitetVedIdentifikasjonsnummer
	} = data

	return (
		<div>
			<SubOverskrift label="Falsk identitet" iconKind="identifikasjon" />
			<div className="person-visning_content">
				{rettIdentitetErUkjent && <TitleValue title="Rett identitet" value={'UKJENT'} />}

				<TitleValue title="Rett fnr/dnr/bost" value={rettIdentitetVedIdentifikasjonsnummer} />

				{rettIdentitetVedOpplysninger && (
					<Fragment>
						<TitleValue title="Rett identitet" value={'Kjent ved personopplysninger'} />

						<Personnavn data={rettIdentitetVedOpplysninger.personnavn} />

						<TitleValue
							title="Fødselsdato"
							value={Formatters.formatStringDates(rettIdentitetVedOpplysninger.foedselsdato)}
						/>
						<TitleValue
							title="Statsborgerskap"
							value={
								rettIdentitetVedOpplysninger.statsborgerskap &&
								rettIdentitetVedOpplysninger.statsborgerskap.join(', ')
							}
						/>

						<TitleValue title="Kjønn" value={rettIdentitetVedOpplysninger.kjoenn} />
					</Fragment>
				)}
			</div>
		</div>
	)
}

import React, { Fragment } from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Personnavn } from './Personnavn'

export const Adressat = ({ adressat }) => {
	if (!adressat) return false

	return (
		<Fragment>
			{adressat.kontaktpersonUtenIdNummerSomAdressat && (
				<Fragment>
					<Personnavn data={adressat.kontaktpersonUtenIdNummerSomAdressat.navn} />

					<TitleValue
						title="Fødselsdato"
						value={Formatters.formatStringDates(
							adressat.kontaktpersonUtenIdNummerSomAdressat.foedselsdato
						)}
					/>
				</Fragment>
			)}

			{adressat.kontaktpersonMedIdNummerSomAdressat && (
				<TitleValue
					title="FNR/DNR/BOST"
					value={adressat.kontaktpersonMedIdNummerSomAdressat.idNummer}
				/>
			)}

			{adressat.advokatSomAdressat && (
				<Fragment>
					<TitleValue
						title="Organisasjonsnavn"
						value={adressat.advokatSomAdressat.organisasjonsnavn}
					/>
					<TitleValue
						title="Organisasjonsnummer"
						value={adressat.advokatSomAdressat.organisasjonsnummer}
					/>
					{adressat.advokatSomAdressat.kontaktperson && (
						<Personnavn data={adressat.advokatSomAdressat.kontaktperson} />
					)}
				</Fragment>
			)}
		</Fragment>
	)
}

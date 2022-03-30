import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Bostedsadresse = () => (
	<section>
		<FormikCheckbox
			name="personinformasjon.bosted.norsk"
			label="Har norsk bostedadresse"
			size="medium"
		/>
		<FormikCheckbox
			name="personinformasjon.bosted.utenlandsk"
			label="Har utenlandsk bostedadresse"
			size="medium"
		/>
		<h4 className="subtittel">Norsk adresse</h4>
		<FormikSelect
			name="personinformasjon.bosted.postnr"
			label="Postnummer"
			kodeverk={AdresseKodeverk.Postnummer}
			optionHeight={50}
			size="medium"
		/>
		<FormikSelect
			name="personinformasjon.bosted.kommunenr"
			label="Kommunenummer"
			kodeverk={AdresseKodeverk.Kommunenummer}
			optionHeight={50}
			size="medium"
		/>
	</section>
)

export const BostedsadressePaths = {
	'personinformasjon.bosted.postnr': 'string',
	'personinformasjon.bosted.kommunenr': 'string',
	'personinformasjon.bosted.utenlandsk': 'boolean',
	'personinformasjon.bosted.norsk': 'boolean',
}

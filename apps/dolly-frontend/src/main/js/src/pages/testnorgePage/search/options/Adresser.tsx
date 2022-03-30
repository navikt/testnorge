import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import React from 'react'
import { AdresseKodeverk, GtKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Adresser = () => (
	<section>
		<h4 className="subtittel">Bostedsadresse</h4>
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
		<h4 className="subtittel">Bydel</h4>
		<FormikSelect
			name="personinformasjon.bosted.bydel"
			label="Bydel"
			kodeverk={GtKodeverk.BYDEL}
			optionHeight={50}
			size="medium"
		/>
	</section>
)

export const AdresserPaths = {
	'personinformasjon.bosted.postnr': 'string',
	'personinformasjon.bosted.kommunenr': 'string',
	'personinformasjon.bosted.bydel': 'string',
}

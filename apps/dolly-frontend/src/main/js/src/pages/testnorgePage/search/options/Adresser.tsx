import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

const bostedPath = 'personinformasjon.bosted'

export const Adresser = () => (
	<section>
		<h4 className="subtittel">Bostedsadresse</h4>
		<FormikSelect
			name={`${bostedPath}.postnummer`}
			label="Postnummer"
			kodeverk={AdresseKodeverk.Postnummer}
			optionHeight={50}
			size="medium"
		/>
		<FormikSelect
			name={`${bostedPath}.kommunenummer`}
			label="Kommunenummer"
			kodeverk={AdresseKodeverk.Kommunenummer}
			optionHeight={50}
			size="medium"
		/>
	</section>
)

export const AdresserPaths = {
	'personinformasjon.bosted.postnummer': 'string',
	'personinformasjon.bosted.kommunenummer': 'string',
}

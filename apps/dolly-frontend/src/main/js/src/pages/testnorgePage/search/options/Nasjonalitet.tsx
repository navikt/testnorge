import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Nasjonalitet = () => (
	<section>
		<FormikSelect
			name="personinformasjon.nasjonalitet.statsborgerskap"
			label="Statsborgerskap"
			kodeverk={AdresseKodeverk.StatsborgerskapLand}
			optionHeight={50}
			size="medium"
		/>
		<FormikCheckbox
			name="personinformasjon.nasjonalitet.innflyttet"
			label="Har innflyttet til Norge"
			size="medium"
		/>
		<FormikCheckbox
			name="personinformasjon.nasjonalitet.utflyttet"
			label="Har utflyttet fra Norge"
			size="medium"
		/>
	</section>
)

export const NasjonalitetPaths = {
	'personinformasjon.nasjonalitet.land': 'string',
	'personinformasjon.nasjonalitet.innflyttet': 'boolean',
	'personinformasjon.nasjonalitet.utflyttet': 'boolean',
}

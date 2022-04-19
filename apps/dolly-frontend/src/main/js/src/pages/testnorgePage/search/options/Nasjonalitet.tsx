import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

const paths = {
	statsborgerskap: 'personinformasjon.nasjonalitet.statsborgerskap',
	inflyttet: 'personinformasjon.nasjonalitet.innflyttet',
	utflyttet: 'personinformasjon.nasjonalitet.utflyttet',
}

export const Nasjonalitet = () => (
	<section>
		<FormikSelect
			name={paths.statsborgerskap}
			label="Statsborgerskap"
			kodeverk={AdresseKodeverk.StatsborgerskapLand}
			optionHeight={50}
			size="medium"
		/>
		<FormikCheckbox name={paths.inflyttet} label="Har innflyttet til Norge" size="medium" />
		<FormikCheckbox name={paths.utflyttet} label="Har utflyttet fra Norge" size="medium" />
	</section>
)

export const NasjonalitetPaths = {
	[paths.statsborgerskap]: 'string',
	[paths.inflyttet]: 'boolean',
	[paths.utflyttet]: 'boolean',
}

import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Relasjoner = () => (
	<section>
		<FormikSelect
			name="personinformasjon.relasjoner.sivilstand"
			label="Sivilstand"
			options={[
				{ value: 'GIFT', label: 'Gift' },
				{ value: 'UGIFT', label: 'Ugift' },
				{ value: 'SEPARERT', label: 'Separert' },
				{ value: 'SKILT', label: 'Skilt' },
			]}
			size="medium"
		/>
		<FormikCheckbox name="personinformasjon.relasjoner.barn" label="Har barn" size="medium" />
		<FormikCheckbox
			name="personinformasjon.relasjoner.doedfoedtBarn"
			label="Har dødfødt barn"
			size="medium"
		/>
	</section>
)

export const RelasjonerPaths = {
	'personinformasjon.relasjoner.sivilstand': 'string',
	'personinformasjon.relasjoner.barn': 'boolean',
	'personinformasjon.relasjoner.doedfoedtBarn': 'boolean',
}

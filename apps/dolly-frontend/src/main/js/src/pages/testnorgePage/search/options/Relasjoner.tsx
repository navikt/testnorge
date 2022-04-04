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
			]}
			size="medium"
		/>
		<FormikCheckbox name="personinformasjon.relasjoner.barn" label="Har barn" size="medium" />
		<FormikCheckbox
			name="personinformasjon.relasjoenr.doedfoedtBarn"
			label="Har dødfødt barn"
			size="medium"
		/>
	</section>
)

export const RelasjonerPaths = {
	'personinformasjon.relasjoner.sivilstand': 'string',
	'personinformasjon.relasjoner.barn': 'boolean',
	'personinformasjon.relasjoenr.doedfoedtBarn': 'boolean',
}

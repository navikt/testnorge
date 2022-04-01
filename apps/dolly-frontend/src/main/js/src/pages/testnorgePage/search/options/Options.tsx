import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Alder = () => (
	<section>
		<FormikTextInput
			name="personinformasjon.alder.fra"
			label="Alder fra"
			visHvisAvhuket={false}
			size="medium"
		/>
		<FormikTextInput
			name="personinformasjon.alder.til"
			label="Alder til"
			visHvisAvhuket={false}
			size="medium"
		/>
		<FormikDatepicker
			name="personinformasjon.alder.foedselsdato.fom"
			label="Fødselsdato fom"
			visHvisAvhuket={false}
			size="medium"
		/>
		<FormikDatepicker
			name="personinformasjon.alder.foedselsdato.tom"
			label="Fødselsdato tom"
			visHvisAvhuket={false}
			size="medium"
		/>
	</section>
)

export const AlderPaths = {
	'personinformasjon.alder.fra': 'string',
	'personinformasjon.alder.til': 'string',
	'personinformasjon.alder.foedselsdato.fom': 'string',
	'personinformasjon.alder.foedselsdato.tom': 'string',
}

export const Sivilstand = () => (
	<section>
		<FormikSelect
			name="personinformasjon.sivilstand.type"
			label="Sivilstand"
			options={[
				{ value: 'GIFT', label: 'Gift' },
				{ value: 'UGIFT', label: 'Ugift' },
			]}
			size="medium"
		/>
	</section>
)

export const SivilstandPaths = {
	'personinformasjon.sivilstand.type': 'string',
}

export const Barn = () => (
	<section>
		<FormikCheckbox name="personinformasjon.barn.barn" label="Har barn" size="medium" />
		<FormikCheckbox
			name="personinformasjon.barn.doedfoedtBarn"
			label="Har dødfødt barn"
			size="medium"
		/>
	</section>
)

export const BarnPaths = {
	'personinformasjon.barn.barn': 'boolean',
	'personinformasjon.barn.doedfoedtBarn': 'boolean',
}

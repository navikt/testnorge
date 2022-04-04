import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

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

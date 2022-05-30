import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

const paths = {
	fra: 'alder.fra',
	til: 'alder.til',
	fom: 'foedsel.fom',
	tom: 'foedsel.tom',
}

export const Alder = () => (
	<section>
		<FormikTextInput name={paths.fra} label="Alder fra" visHvisAvhuket={false} size="medium" />
		<FormikTextInput name={paths.til} label="Alder til" visHvisAvhuket={false} size="medium" />
		<FormikDatepicker
			name={paths.fom}
			label="Fødselsdato fom"
			visHvisAvhuket={false}
			size="medium"
		/>
		<FormikDatepicker
			name={paths.tom}
			label="Fødselsdato tom"
			visHvisAvhuket={false}
			size="medium"
		/>
	</section>
)

export const AlderPaths = {
	[paths.fra]: 'string',
	[paths.til]: 'string',
	[paths.fom]: 'string',
	[paths.tom]: 'string',
}

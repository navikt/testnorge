import React, { BaseSyntheticEvent } from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikProps } from 'formik'

const paths = {
	fra: 'alder.fra',
	til: 'alder.til',
	fom: 'foedsel.fom',
	tom: 'foedsel.tom',
}

type Props = {
	formikBag: FormikProps<{}>
}

export const Alder = ({ formikBag }: Props) => (
	<section>
		<FormikTextInput
			name={paths.fra}
			label="Alder fra"
			visHvisAvhuket={false}
			size="medium"
			useControlled
			onKeyPress={(event: BaseSyntheticEvent<KeyboardEvent>) => {
				event.nativeEvent.code === 'Enter' &&
					!Object.keys(formikBag.errors).length &&
					formikBag.handleSubmit()
			}}
		/>
		<FormikTextInput
			name={paths.til}
			label="Alder til"
			visHvisAvhuket={false}
			size="medium"
			useControlled
			onKeyPress={(event: BaseSyntheticEvent<KeyboardEvent>) => {
				event.nativeEvent.code === 'Enter' &&
					!Object.keys(formikBag.errors).length &&
					formikBag.handleSubmit()
			}}
		/>
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

export const AlderPaths = Object.values(paths)

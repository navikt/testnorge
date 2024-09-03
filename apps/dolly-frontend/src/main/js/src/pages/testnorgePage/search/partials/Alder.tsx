import React, { BaseSyntheticEvent } from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { UseFormReturn } from 'react-hook-form/dist/types'

const paths = {
	fra: 'alder.fra',
	til: 'alder.til',
	fom: 'foedselsdato.fom',
	tom: 'foedselsdato.tom',
}

type Props = {
	formMethods: UseFormReturn
}

export const Alder = ({ formMethods }: Props) => (
	<section>
		<FormTextInput
			name={paths.fra}
			label="Alder fra"
			visHvisAvhuket={false}
			size="medium"
			onKeyPress={(event: BaseSyntheticEvent<KeyboardEvent>) => {
				event.nativeEvent.code === 'Enter' &&
					formMethods.formState.isValid &&
					formMethods.handleSubmit()
			}}
		/>
		<FormTextInput
			name={paths.til}
			label="Alder til"
			visHvisAvhuket={false}
			size="medium"
			onKeyPress={(event: BaseSyntheticEvent<KeyboardEvent>) => {
				event.nativeEvent.code === 'Enter' &&
					formMethods.formState.isValid &&
					formMethods.handleSubmit()
			}}
		/>
		<FormDatepicker name={paths.fom} label="Fødselsdato fom" visHvisAvhuket={false} size="medium" />
		<FormDatepicker name={paths.tom} label="Fødselsdato tom" visHvisAvhuket={false} size="medium" />
	</section>
)

export const AlderPaths = Object.values(paths)

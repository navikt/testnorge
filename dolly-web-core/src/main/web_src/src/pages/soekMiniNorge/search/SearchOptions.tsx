import * as React from 'react'
// @ts-ignore
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikProps } from 'formik'

interface SearchOptionsProps {
	formikBag: FormikProps<{}>
}

export const SearchOptions = ({ formikBag }: SearchOptionsProps) => {
	return (
		<React.Fragment>
			<FormikTextInput name='navn.fornavn' label="Fornavn" />
			<FormikTextInput name='navn.mellomnavn' label="Mellomnavn" />
			<FormikTextInput name='navn.slektsnavn' label="Etternavn" />
		</React.Fragment>
		)
}
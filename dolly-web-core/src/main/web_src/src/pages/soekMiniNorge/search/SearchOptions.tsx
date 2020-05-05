import * as React from 'react'
// @ts-ignore
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikProps } from 'formik'
import NavButton from '~/components/ui/button/NavButton/NavButton'

interface SearchOptionsProps {
	formikBag: FormikProps<{}>
	onSubmit: (x:any) => void
}

export const SearchOptions = (props: SearchOptionsProps) => {
	return (
		<React.Fragment>
			<div>
				<h2>Personinformasjon</h2>
				<h3>Navn</h3>
				<FormikTextInput name="navn.fornavn" label="Fornavn" />
				<FormikTextInput name="navn.mellomnavn" label="Mellomnavn" />
				<FormikTextInput name="navn.slektsnavn" label="Etternavn" />
			</div>
			<div className="search-button">
				<NavButton onClick={() => props.onSubmit(props.formikBag.values)}>Søk</NavButton>
			</div>
		</React.Fragment>
	)
}
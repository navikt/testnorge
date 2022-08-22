import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikProps } from 'formik'

interface PostboksadresseValues {
	formikBag: FormikProps<{}>
	path: string
}

export const Postboksadresse = ({ formikBag, path }: PostboksadresseValues) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput name={`${path}.postbokseier`} label="Postbokseier" size="large" />
			<FormikTextInput name={`${path}.postboks`} label="Postboks" />
			<FormikSelect
				name={`${path}.postnummer`}
				label="Postnummer"
				kodeverk={AdresseKodeverk.Postnummer}
				size="large"
			/>
		</div>
	)
}

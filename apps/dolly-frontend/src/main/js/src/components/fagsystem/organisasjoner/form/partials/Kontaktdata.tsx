import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { kontaktPaths } from '../paths'

type Kontaktdata = {
	path: string
}

export const Kontaktdata = ({ path }: Kontaktdata) => {
	return (
		<Kategori title="Kontaktdata" vis={kontaktPaths}>
			<FormikTextInput name={`${path}.telefon`} label="Telefon" size="large" type="number" />
			<FormikTextInput name={`${path}.epost`} label="E-postadresse" size="large" />
			<FormikTextInput name={`${path}.nettside`} label="Internettadresse" size="large" />
		</Kategori>
	)
}

import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

const getSistePostlinje = formikBag => {
	let nr = 3
	formikBag.values.tpsf.postadresse[0].postLinje2
		? (nr = 3)
		: formikBag.values.tpsf.postadresse[0].postLinje1
		? (nr = 2)
		: (nr = 1)
	return nr
}

export const Postadresser = ({ formikBag }) => (
	<Kategori title="Postadresse" vis="tpsf.postadresse">
		<FormikSelect name="tpsf.postadresse[0].postLand" label="Land" kodeverk="Landkoder" />
		<FormikTextInput name="tpsf.postadresse[0].postLinje1" label="Postlinje1" />
		<FormikTextInput name="tpsf.postadresse[0].postLinje2" label="Postlinje2" />

		{formikBag.values.tpsf.postadresse[0].postLand !== 'NOR' && (
			<React.Fragment>
				<FormikTextInput name="tpsf.postadresse[0].postLinje3" label="Postnummer" />
			</React.Fragment>
		)}

		{formikBag.values.tpsf.postadresse[0].postLand === 'NOR' && (
			<FormikSelect
				name={`tpsf.postadresse[0].postLinje${getSistePostlinje(formikBag)}`}
				label="Postnummer/Poststed"
				kodeverk="Postnummer"
			/>
		)}
	</Kategori>
)

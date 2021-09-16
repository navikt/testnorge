import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const Postadresser = ({ formikBag }) => {
	const handleAfterChange = (selected) => {
		return formikBag.setFieldValue(
			'tpsf.postadresse[0].postLinje3',
			selected.value + ' ' + selected.data
		)
	}

	return (
		<Kategori title="Postadresse" vis="tpsf.postadresse">
			<FormikSelect
				name="tpsf.postadresse[0].postLand"
				label="Land"
				kodeverk={AdresseKodeverk.PostadresseLand}
				isClearable={false}
				size="large"
			/>

			<FormikTextInput name="tpsf.postadresse[0].postLinje1" label="Postlinje 1" />
			<FormikTextInput name="tpsf.postadresse[0].postLinje2" label="Postlinje 2" />

			{formikBag.values.tpsf.postadresse[0].postLand !== 'NOR' && (
				<FormikTextInput name="tpsf.postadresse[0].postLinje3" label="Postlinje 3" />
			)}

			{formikBag.values.tpsf.postadresse[0].postLand === 'NOR' && (
				<FormikSelect
					name="tpsf.postadresse[0].postLinje3"
					label={'Postnummer/sted'}
					value={formikBag.values.tpsf.postadresse[0].postLinje3.substring(0, 4)}
					kodeverk={AdresseKodeverk.Postnummer}
					size="large"
					afterChange={handleAfterChange}
					isClearable={false}
				/>
			)}
		</Kategori>
	)
}

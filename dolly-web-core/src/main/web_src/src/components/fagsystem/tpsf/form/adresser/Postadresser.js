import React from 'react'

import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const Postadresser = ({ formikBag }) => {
	const handleAfterChange = selected => {
		return formikBag.setFieldValue(
			'tpsf.postadresse[0].postLinje3',
			selected.value + ' ' + selected.data
		)
	}
	return (
		<Kategori title="Postadresse" vis="tpsf.postadresse">
			<FormikSelect name="tpsf.postadresse[0].postLand" label="Land" kodeverk="Landkoder" />
			{formikBag.values.tpsf.postadresse[0].postLand !== 'NOR' && (
				<React.Fragment>
					<FormikTextInput name="tpsf.postadresse[0].postLinje1" label="Postlinje 1" />
					<FormikTextInput name="tpsf.postadresse[0].postLinje2" label="Postlinje 2" />
					<FormikTextInput name="tpsf.postadresse[0].postLinje3" label="Postlinje 3" />
				</React.Fragment>
			)}

			{formikBag.values.tpsf.postadresse[0].postLand === 'NOR' && (
				<React.Fragment>
					<FormikTextInput
						name="tpsf.postadresse[0].postLinje1"
						label="Postlinje 1"
						size="grow"
						label="Postlinje 1"
					/>
					<FormikTextInput
						name="tpsf.postadresse[0].postLinje2"
						label="Postlinje 2"
						size="grow"
						label="Postlinje 2"
					/>
					<FormikSelect
						name="formikBag.values.tpsf.postadresse[0].postLinje3"
						label={'Postnummer/sted'}
						kodeverk="Postnummer"
						size="grow"
						afterChange={handleAfterChange}
					/>
				</React.Fragment>
			)}
		</Kategori>
	)
}

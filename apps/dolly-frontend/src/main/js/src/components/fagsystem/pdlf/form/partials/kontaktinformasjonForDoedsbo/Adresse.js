import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import _get from 'lodash/get'

export const Adresse = ({ formikBag, path }) => {
	const handleAfterChange = (selected) => {
		return formikBag.setFieldValue(`${path}.poststedsnavn`, selected.data)
	}

	return (
		<Kategori title="Adresse">
			<FormikSelect
				name={`${path}.landkode`}
				label="Land"
				kodeverk={AdresseKodeverk.PostadresseLand}
				size="large"
				isClearable={false}
			/>
			<FormikTextInput name={`${path}.adresselinje1`} label="Adresselinje 1" />
			<FormikTextInput name={`${path}.adresselinje2`} label="Adresselinje 2" />
			{_get(formikBag.values, `${path}.landkode`) === 'NOR' ? (
				<FormikSelect
					name={`${path}.postnummer`}
					label="Postnummer og -sted"
					kodeverk={AdresseKodeverk.Postnummer}
					afterChange={handleAfterChange}
					isClearable={false}
					size="large"
				/>
			) : (
				<>
					<FormikTextInput name={`${path}.postnummer`} label="Postnummer" />
					<FormikTextInput name={`${path}.poststedsnavn`} label="Poststed" />
				</>
			)}
		</Kategori>
	)
}

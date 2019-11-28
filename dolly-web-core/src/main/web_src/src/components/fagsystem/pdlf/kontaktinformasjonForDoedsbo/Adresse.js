import React from 'react'
import _get from 'lodash'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const Adresse = ({ formikBag }) => {
	const handleAfterChange = selected => {
		return formikBag.setFieldValue(
			'pdlforvalter.kontaktinformasjonForDoedsbo.poststed',
			selected.data
		)
	}

	return (
		<Kategori title="Adresse">
			<FormikSelect
				name="pdlforvalter.kontaktinformasjonForDoedsbo.landkode"
				label="Land"
				kodeverk="Landkoder"
			/>
			<div>
				<FormikTextInput
					name="pdlforvalter.kontaktinformasjonForDoedsbo.adresselinje1"
					label="Adresselinje 1"
				/>
				<FormikTextInput
					name="pdlforvalter.kontaktinformasjonForDoedsbo.adresselinje2"
					label="Adresselinje 2"
				/>
			</div>
			{formikBag.values.pdlforvalter.kontaktinformasjonForDoedsbo.landkode === 'NOR' ? (
				<FormikSelect
					name="pdlforvalter.kontaktinformasjonForDoedsbo.postnummer"
					label="Postnummer og -sted"
					kodeverk="Postnummer"
					afterChange={handleAfterChange}
					isClearable={false}
				/>
			) : (
				<div>
					<FormikTextInput
						name="pdlforvalter.kontaktinformasjonForDoedsbo.postnummer"
						label="Postnummer"
					/>
					<FormikTextInput
						name="pdlforvalter.kontaktinformasjonForDoedsbo.poststed"
						label="Poststed"
					/>
				</div>
			)}
		</Kategori>
	)
}

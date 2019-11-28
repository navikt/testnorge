import React from 'react'
import _get from 'lodash'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export const Adressat = ({ formikBag }) => {
	const adressatType =
		formikBag.values.pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType

	const handleAfterChange = ({ value }) => {
		if (value === 'ADVOKAT' || value === 'ORGANISASJON')
			formikBag.setFieldValue('pdlforvalter.kontaktinformasjonForDoedsbo.adressat', {
				adressatType: value,
				kontaktperson: { fornavn: '', mellomnavn: '', etternavn: '' },
				organisasjonsnavn: '',
				organisasjonsnummer: ''
			})
		else if (value === 'PERSON_UTENID')
			formikBag.setFieldValue('pdlforvalter.kontaktinformasjonForDoedsbo.adressat', {
				adressatType: value,
				navn: { fornavn: '', mellomnavn: '', etternavn: '' },
				foedselsdato: ''
			})
		else
			formikBag.setFieldValue('pdlforvalter.kontaktinformasjonForDoedsbo.adressat', {
				adressatType: value,
				idnummer: ''
			})
	}

	return (
		<Kategori title="Adressat">
			<FormikSelect
				name="pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType"
				label="Adressattype"
				options={Options('adressatType')}
				afterChange={handleAfterChange}
				isClearable={false}
			/>

			{(adressatType === 'ADVOKAT' || adressatType === 'ORGANISASJON') && (
				<React.Fragment>
					{navnForm('pdlforvalter.kontaktinformasjonForDoedsbo.adressat.kontaktperson')}
					<FormikTextInput
						name="pdlforvalter.kontaktinformasjonForDoedsbo.adressat.organisasjonsnavn"
						label="Organisasjonsnavn"
					/>
					<FormikTextInput
						name="pdlforvalter.kontaktinformasjonForDoedsbo.adressat.organisasjonsnummer"
						label="Organisasjonsnummer"
					/>
				</React.Fragment>
			)}

			{adressatType === 'PERSON_MEDID' && (
				<FormikTextInput
					name="pdlforvalter.kontaktinformasjonForDoedsbo.adressat.idnummer"
					label="Fnr/dnr/bost"
				/>
			)}
			{adressatType === 'PERSON_UTENID' && (
				<React.Fragment>
					{navnForm('pdlforvalter.kontaktinformasjonForDoedsbo.adressat.navn')}
					<FormikDatepicker
						name="pdlforvalter.kontaktinformasjonForDoedsbo.adressat.foedselsdato"
						label="Fødselsdato"
					/>
				</React.Fragment>
			)}
		</Kategori>
	)
}

const navnForm = path => {
	return (
		<div>
			<FormikTextInput name={`${path}.fornavn`} label="Fornavn" />
			<FormikTextInput name={`${path}.mellomnavn`} label="Mellomnavn" />
			<FormikTextInput name={`${path}.etternavn`} label="Etternavn" />
		</div>
	)
}

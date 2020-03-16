import React from 'react'
import _get from 'lodash'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { DollyApi } from "~/service/Api";
import FasteDatasettSelect from '~/components/fasteDatasett/FasteDatasettSelect'

export const Adressat = ({ formikBag }) => {
	const adressatType =
		formikBag.values.pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType

	const handleAfterChange = ({ value }) => {
		if (value === 'ADVOKAT' || value === 'ORGANISASJON')
			formikBag.setFieldValue('pdlforvalter.kontaktinformasjonForDoedsbo.adressat', {
				adressatType: value,
				// kontaktperson: { fornavn: '', mellomnavn: '', etternavn: '' },
				kontaktperson: {fulltNavn: ''},
				organisasjonsnavn: '',
				organisasjonsnummer: ''
			})
		else if (value === 'PERSON_UTENID')
			formikBag.setFieldValue('pdlforvalter.kontaktinformasjonForDoedsbo.adressat', {
				adressatType: value,
				navn: {fulltNavn: ''},
				// navn: '',
				foedselsdato: ''
			})
		else if (value === 'PERSON_MEDID')
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
				onChange={handleAfterChange}
				isClearable={false}
			/>

			{(adressatType === 'ADVOKAT' || adressatType === 'ORGANISASJON') && (
				<React.Fragment>
					<FasteDatasettSelect
						name="pdlforvalter.kontaktinformasjonForDoedsbo.adressat.navn.fulltnavn"
						label="Navn"
						endepunkt={ DollyApi.getPersonnavn }
						type="navn"
					/>
					<div>
						<FormikTextInput
							name="pdlforvalter.kontaktinformasjonForDoedsbo.adressat.organisasjonsnavn"
							label="Organisasjonsnavn"
						/>
						<FormikTextInput
							name="pdlforvalter.kontaktinformasjonForDoedsbo.adressat.organisasjonsnummer"
							label="Organisasjonsnummer"
						/>
					</div>
				</React.Fragment>
			)}

			{adressatType === 'PERSON_MEDID' && (
				<FasteDatasettSelect
					name="pdlforvalter.kontaktinformasjonForDoedsbo.adressat.idnummer"
					label="Navn og id"
					endepunkt={ DollyApi.getFasteDatasettTPS }
					type="navnOgFnr"
				/>
			)}
			{adressatType === 'PERSON_UTENID' && (
				<React.Fragment>
					<FasteDatasettSelect
						name="pdlforvalter.kontaktinformasjonForDoedsbo.adressat.navn.fulltnavn"
						label="Navn"
						endepunkt={ DollyApi.getPersonnavn }
						type="navn"
					/>
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

import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { DollyApi } from "~/service/Api";
import FasteDatasettSelect from '~/components/fasteDatasett/FasteDatasettSelect'
import {getNavnOgFnrListe, getNavnListe} from "../filterMethods"
import {getPlaceholder} from "../utils"

export const Adressat = ({ formikBag }) => {
	const addressatPath = 'pdlforvalter.kontaktinformasjonForDoedsbo.adressat'
	const adressatType =
		formikBag.values.pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType

	const handleAfterChange = ({ value }) => {
		if (value === 'ADVOKAT' || value === 'ORGANISASJON')
			formikBag.setFieldValue(addressatPath, {
				adressatType: value,
				kontaktperson: '',
				organisasjonsnavn: '',
				organisasjonsnummer: ''
			})
		else if (value === 'PERSON_UTENID')
			formikBag.setFieldValue(addressatPath, {
				adressatType: value,
				navn: '',
				foedselsdato: ''
			})
		else if (value === 'PERSON_MEDID')
			formikBag.setFieldValue(addressatPath, {
				adressatType: value,
				idnummer: ''
			})
	}

	return (
		<Kategori title="Adressat">
			<FormikSelect
				name={`${addressatPath}.adressatType`}
				label="Adressattype"
				options={Options('adressatType')}
				onChange={handleAfterChange}
				isClearable={false}
			/>

			{(adressatType === 'ADVOKAT' || adressatType === 'ORGANISASJON') && (
				<React.Fragment>
					<FasteDatasettSelect
						name={`${addressatPath}.kontaktperson`}
						label="Navn"
						endepunkt={DollyApi.getPersonnavn}
						filterMethod={getNavnListe}
						size="large"
						placeholder={getPlaceholder(formikBag.values,`${addressatPath}.kontaktperson`)}
					/>
					<div>
						<FormikTextInput
							name={`${addressatPath}.organisasjonsnavn`}
							label="Organisasjonsnavn"
						/>
						<FormikTextInput
							name={`${addressatPath}.organisasjonsnummer`}
							label="Organisasjonsnummer"
						/>
					</div>
				</React.Fragment>
			)}

			{adressatType === 'PERSON_MEDID' && (
				<FasteDatasettSelect
					name={`${addressatPath}.idnummer`}
					label="Navn og id"
					endepunkt={DollyApi.getFasteDatasettTPS}
					filterMethod={getNavnOgFnrListe}
					size="large"
				/>
			)}
			{adressatType === 'PERSON_UTENID' && (
				<React.Fragment>
					<FasteDatasettSelect
						name={`${addressatPath}.navn`}
						label="Navn"
						endepunkt={DollyApi.getPersonnavn}
						filterMethod={getNavnListe}
						size="large"
						placeholder={getPlaceholder(formikBag.values,`${addressatPath}.navn`)}
					/>
					<FormikDatepicker
						name={`${addressatPath}.foedselsdato`}
						label="Fødselsdato"
					/>
				</React.Fragment>
			)}
		</Kategori>
	)
}

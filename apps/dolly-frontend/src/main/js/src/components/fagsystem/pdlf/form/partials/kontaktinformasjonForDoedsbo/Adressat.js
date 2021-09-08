import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { getPlaceholder, setNavn, setValue } from '../utils'
import _get from 'lodash/get'

export const Adressat = ({ formikBag }) => {
	const addressatPath = 'pdlforvalter.kontaktinformasjonForDoedsbo.adressat'
	const adressatType =
		formikBag.values.pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType

	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const navnOptions = SelectOptionsOppslag.formatOptions('personnavn', navnInfo)
	const dollyGruppeInfo = SelectOptionsOppslag.hentGruppe()
	const navnOgFnrOptions = SelectOptionsOppslag.formatOptions('navnOgFnr', dollyGruppeInfo)

	const handleAfterChange = ({ value }) => {
		if (value === 'ADVOKAT' || value === 'ORGANISASJON')
			formikBag.setFieldValue(addressatPath, {
				adressatType: value,
				kontaktperson: { fornavn: '', mellomnavn: '', etternavn: '' },
				organisasjonsnavn: '',
				organisasjonsnummer: '',
			})
		else if (value === 'PERSON_UTENID')
			formikBag.setFieldValue(addressatPath, {
				adressatType: value,
				navn: { fornavn: '', mellomnavn: '', etternavn: '' },
				foedselsdato: '',
			})
		else if (value === 'PERSON_MEDID')
			formikBag.setFieldValue(addressatPath, {
				adressatType: value,
				idnummer: '',
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
					<DollySelect
						name={`${addressatPath}.kontaktperson.fornavn`}
						label="Navn"
						options={navnOptions}
						size="large"
						placeholder={getPlaceholder(formikBag.values, `${addressatPath}.kontaktperson`)}
						isLoading={navnInfo.loading}
						onChange={(navn) =>
							setNavn(navn, `${addressatPath}.kontaktperson`, formikBag.setFieldValue)
						}
						value={_get(formikBag.values, `${addressatPath}.kontaktperson.fornavn`)}
						isClearable={false}
						optionHeight={50}
					/>
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
				<DollySelect
					name={`${addressatPath}.idnummer`}
					label="Navn og id"
					options={navnOgFnrOptions}
					size="large"
					isLoading={dollyGruppeInfo.loading}
					onChange={(id) => setValue(id, `${addressatPath}.idnummer`, formikBag.setFieldValue)}
					value={_get(formikBag.values, `${addressatPath}.idnummer`)}
					isClearable={false}
				/>
			)}
			{adressatType === 'PERSON_UTENID' && (
				<React.Fragment>
					<DollySelect
						name={`${addressatPath}.navn.fornavn`}
						label="Navn"
						options={navnOptions}
						size="large"
						placeholder={getPlaceholder(formikBag.values, `${addressatPath}.navn`)}
						isLoading={navnInfo.loading}
						onChange={(navn) => setNavn(navn, `${addressatPath}.navn`, formikBag.setFieldValue)}
						value={_get(formikBag.values, `${addressatPath}.navn.fornavn`)}
						isClearable={false}
					/>
					<FormikDatepicker name={`${addressatPath}.foedselsdato`} label="Fødselsdato" />
				</React.Fragment>
			)}
		</Kategori>
	)
}

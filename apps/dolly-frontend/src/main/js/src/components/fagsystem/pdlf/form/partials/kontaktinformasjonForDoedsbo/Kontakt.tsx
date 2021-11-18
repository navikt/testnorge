import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { getPlaceholder, setNavn, setValue } from '../utils'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { initialPdlPerson } from '~/components/fagsystem/pdlf/form/initialValues'
import { PdlPersonForm } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonForm'

const initialOrganisasjon = {
	organisasjonsnummer: null,
	organisasjonsnavn: null,
	kontaktperson: {
		fornavn: null,
		mellomnavn: null,
		etternavn: null,
	},
}

const initialPerson = {
	// identifikasjonsnummer: null,
	foedselsdato: null,
	navn: {
		fornavn: null,
		mellomnavn: null,
		etternavn: null,
	},
}

const initialNyPerson = { nyKontaktperson: initialPdlPerson }

export const Kontakt = ({ formikBag, path }) => {
	const advokatPath = `${path}.advokatSomKontakt`
	const personPath = `${path}.personSomKontakt`
	const organisasjonPath = `${path}.organisasjonSomKontakt`

	// todo: refactor
	const kontaktType = _has(formikBag.values, advokatPath)
		? 'ADVOKAT'
		: _has(formikBag.values, organisasjonPath)
		? 'ORGANISASJON'
		: _has(formikBag.values, personPath) && _has(formikBag.values, `${personPath}.foedselsdato`)
		? 'PERSON'
		: _has(formikBag.values, personPath) && _has(formikBag.values, `${personPath}.nyKontaktperson`)
		? 'NY_PERSON'
		: null

	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const navnOptions = SelectOptionsOppslag.formatOptions('personnavn', navnInfo)
	// const dollyGruppeInfo = SelectOptionsOppslag.hentGruppe()
	// const navnOgFnrOptions = SelectOptionsOppslag.formatOptions('navnOgFnr', dollyGruppeInfo)

	const handleAfterChange = ({ value }) => {
		if (value !== kontaktType) {
			if (value === 'ADVOKAT') {
				formikBag.setFieldValue(`${path}.advokatSomKontakt`, initialOrganisasjon)
				formikBag.setFieldValue(`${path}.personSomKontakt`, undefined)
				formikBag.setFieldValue(`${path}.organisasjonSomKontakt`, undefined)
			} else if (value === 'ORGANISASJON') {
				formikBag.setFieldValue(`${path}.organisasjonSomKontakt`, initialOrganisasjon)
				formikBag.setFieldValue(`${path}.advokatSomKontakt`, undefined)
				formikBag.setFieldValue(`${path}.personSomKontakt`, undefined)
			} else if (value === 'PERSON') {
				formikBag.setFieldValue(`${path}.personSomKontakt`, initialPerson)
				formikBag.setFieldValue(`${path}.advokatSomKontakt`, undefined)
				formikBag.setFieldValue(`${path}.organisasjonSomKontakt`, undefined)
			} else if (value === 'NY_PERSON') {
				formikBag.setFieldValue(`${path}.personSomKontakt`, initialNyPerson)
				formikBag.setFieldValue(`${path}.advokatSomKontakt`, undefined)
				formikBag.setFieldValue(`${path}.organisasjonSomKontakt`, undefined)
			}
		}
	}

	return (
		<Kategori title="Kontakt">
			<FormikSelect
				name={`${path}.kontaktType`}
				label="Kontakttype"
				value={kontaktType}
				options={Options('kontaktType')}
				onChange={handleAfterChange}
				isClearable={false}
			/>
			{/*//TODO: Sett feil hvis ikke valgt!!*/}
			{kontaktType === 'ADVOKAT' && (
				<>
					<FormikTextInput
						name={`${advokatPath}.organisasjonsnummer`}
						label="Organisasjonsnummer"
					/>
					<FormikTextInput name={`${advokatPath}.organisasjonsnavn`} label="Organisasjonsnavn" />
					<DollySelect
						name={`${advokatPath}.kontaktperson.fornavn`}
						label="Kontaktperson navn"
						options={navnOptions}
						size="large"
						placeholder={getPlaceholder(formikBag.values, `${advokatPath}.kontaktperson`)}
						isLoading={navnInfo.loading}
						onChange={(navn) =>
							setNavn(navn, `${advokatPath}.kontaktperson`, formikBag.setFieldValue)
						}
						value={_get(formikBag.values, `${advokatPath}.kontaktperson.fornavn`)}
						isClearable={false}
					/>
				</>
			)}

			{kontaktType === 'ORGANISASJON' && (
				<>
					<FormikTextInput
						name={`${organisasjonPath}.organisasjonsnummer`}
						label="Organisasjonsnummer"
					/>
					<FormikTextInput
						name={`${organisasjonPath}.organisasjonsnavn`}
						label="Organisasjonsnavn"
					/>
					<DollySelect
						name={`${organisasjonPath}.kontaktperson.fornavn`}
						label="Kontaktperson navn"
						options={navnOptions}
						size="large"
						placeholder={getPlaceholder(formikBag.values, `${organisasjonPath}.kontaktperson`)}
						isLoading={navnInfo.loading}
						onChange={(navn) =>
							setNavn(navn, `${organisasjonPath}.kontaktperson`, formikBag.setFieldValue)
						}
						value={_get(formikBag.values, `${organisasjonPath}.kontaktperson.fornavn`)}
					/>
				</>
			)}

			{kontaktType === 'PERSON' && (
				<>
					<FormikDatepicker name={`${personPath}.foedselsdato`} label="Fødselsdato" />
					<DollySelect
						name={`${personPath}.navn.fornavn`}
						label="Kontaktperson navn"
						options={navnOptions}
						size="large"
						placeholder={getPlaceholder(formikBag.values, `${personPath}.navn`)}
						isLoading={navnInfo.loading}
						onChange={(navn) => setNavn(navn, `${personPath}.navn`, formikBag.setFieldValue)}
						value={_get(formikBag.values, `${personPath}.navn`)}
						isClearable={false}
					/>
					{/*<FormikTextInput*/}
					{/*	name={`${personPath}.identifikasjonsnummer`}*/}
					{/*	label="Identifikasjonsnummer"*/}
					{/*/>*/}
					{/*// todo: Eget field for navn??*/}
				</>
			)}

			{kontaktType === 'NY_PERSON' && (
				<PdlPersonForm path={`${personPath}.nyKontaktperson`} formikBag={formikBag} />
			)}
		</Kategori>
	)
}

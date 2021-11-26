import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { getPlaceholder, setNavn } from '../utils'
import _get from 'lodash/get'
import { initialPdlPerson } from '~/components/fagsystem/pdlf/form/initialValues'
import { OrganisasjonSelect } from '~/components/organisasjonSelect'
import _cloneDeep from 'lodash/cloneDeep'
import _set from 'lodash/set'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { FormikProps } from 'formik'

interface KontaktValues {
	formikBag: FormikProps<{}>
	path: string
}

type TypeValues = {
	value: string
	label: string
}

type OrgValues = {
	orgnr: string
	navn: string
}

const initialOrganisasjon = {
	organisasjonsnummer: null as string,
	organisasjonsnavn: null as string,
	kontaktperson: {
		fornavn: null as string,
		mellomnavn: null as string,
		etternavn: null as string,
	},
}

const initialPerson = {
	foedselsdato: null as string,
	navn: {
		fornavn: null as string,
		mellomnavn: null as string,
		etternavn: null as string,
	},
}

const initialNyPerson = {
	nyKontaktperson: initialPdlPerson,
}

export const Kontakt = ({ formikBag, path }: KontaktValues) => {
	const advokatPath = `${path}.advokatSomKontakt`
	const organisasjonPath = `${path}.organisasjonSomKontakt`
	const personPath = `${path}.personSomKontakt`

	const kontaktType = _get(formikBag.values, `${path}.kontaktType`)

	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const navnOptions = SelectOptionsOppslag.formatOptions('personnavn', navnInfo)

	const handleAfterChange = (type: TypeValues) => {
		const { value } = type
		const kontaktinfo = _get(formikBag.values, path)
		const kontaktinfoClone = _cloneDeep(kontaktinfo)

		if (value !== kontaktType) {
			_set(kontaktinfoClone, 'kontaktType', value)
			if (value === 'ADVOKAT') {
				_set(kontaktinfoClone, 'advokatSomKontakt', initialOrganisasjon)
				_set(kontaktinfoClone, 'organisasjonSomKontakt', undefined)
				_set(kontaktinfoClone, 'personSomKontakt', undefined)
			} else if (value === 'ORGANISASJON') {
				_set(kontaktinfoClone, 'organisasjonSomKontakt', initialOrganisasjon)
				_set(kontaktinfoClone, 'advokatSomKontakt', undefined)
				_set(kontaktinfoClone, 'personSomKontakt', undefined)
			} else if (value === 'PERSON_FDATO') {
				_set(kontaktinfoClone, 'personSomKontakt', initialPerson)
				_set(kontaktinfoClone, 'advokatSomKontakt', undefined)
				_set(kontaktinfoClone, 'organisasjonSomKontakt', undefined)
			} else if (value === 'NY_PERSON') {
				_set(kontaktinfoClone, 'personSomKontakt', initialNyPerson)
				_set(kontaktinfoClone, 'advokatSomKontakt', undefined)
				_set(kontaktinfoClone, 'organisasjonSomKontakt', undefined)
			}
		}
		formikBag.setFieldValue(path, kontaktinfoClone)
	}

	const organisasjonHandleChange = (values: OrgValues, path: string) => {
		formikBag.setFieldValue(`${path}.organisasjonsnummer`, values.orgnr)
		formikBag.setFieldValue(`${path}.organisasjonsnavn`, values.navn)
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
				size="medium"
			/>
			{kontaktType === 'ADVOKAT' && (
				<>
					<OrganisasjonSelect
						path={`${advokatPath}.organisasjonsnummer`}
						label="Organisasjonsnummer"
						afterChange={(val: OrgValues) => organisasjonHandleChange(val, advokatPath)}
					/>
					<DollySelect
						name={`${advokatPath}.kontaktperson.fornavn`}
						label="Kontaktperson navn"
						options={navnOptions}
						size="xlarge"
						placeholder={getPlaceholder(formikBag.values, `${advokatPath}.kontaktperson`)}
						isLoading={navnInfo.loading}
						onChange={(navn: string) =>
							setNavn(navn, `${advokatPath}.kontaktperson`, formikBag.setFieldValue)
						}
						value={_get(formikBag.values, `${advokatPath}.kontaktperson.fornavn`)}
					/>
				</>
			)}

			{kontaktType === 'ORGANISASJON' && (
				<>
					<OrganisasjonSelect
						path={`${organisasjonPath}.organisasjonsnummer`}
						label="Organisasjonsnummer"
						afterChange={(val: OrgValues) => organisasjonHandleChange(val, organisasjonPath)}
					/>
					<DollySelect
						name={`${organisasjonPath}.kontaktperson.fornavn`}
						label="Kontaktperson navn"
						options={navnOptions}
						size="xlarge"
						placeholder={getPlaceholder(formikBag.values, `${organisasjonPath}.kontaktperson`)}
						isLoading={navnInfo.loading}
						onChange={(navn: string) =>
							setNavn(navn, `${organisasjonPath}.kontaktperson`, formikBag.setFieldValue)
						}
						value={_get(formikBag.values, `${organisasjonPath}.kontaktperson.fornavn`)}
					/>
				</>
			)}

			{kontaktType === 'PERSON_FDATO' && (
				<>
					<FormikDatepicker name={`${personPath}.foedselsdato`} label="Fødselsdato" />
					<DollySelect
						name={`${personPath}.navn.fornavn`}
						label="Kontaktperson navn"
						options={navnOptions}
						size="xlarge"
						placeholder={getPlaceholder(formikBag.values, `${personPath}.navn`)}
						isLoading={navnInfo.loading}
						onChange={(navn: string) =>
							setNavn(navn, `${personPath}.navn`, formikBag.setFieldValue)
						}
						value={_get(formikBag.values, `${personPath}.navn.fornavn`)}
					/>
				</>
			)}

			{kontaktType === 'NY_PERSON' && (
				<PdlPersonExpander
					path={`${personPath}.nyKontaktperson`}
					label="KONTAKTPERSON"
					formikBag={formikBag}
				/>
			)}
		</Kategori>
	)
}

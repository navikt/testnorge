import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getPlaceholder, setNavn } from '../utils'
import * as _ from 'lodash-es'
import {
	initialNyPerson,
	initialOrganisasjon,
	initialPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { OrganisasjonSelect } from '@/components/organisasjonSelect'
import { FormikProps } from 'formik'
import { PdlNyPerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { useEffect } from 'react'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'

interface KontaktValues {
	formikBag: FormikProps<any>
	path: string
	eksisterendeNyPerson?: any
}

type TypeValues = {
	value: string
	label: string
}

type OrgValues = {
	orgnr: string
	navn: string
}

export const Kontakt = ({ formMethods, path, eksisterendeNyPerson = null }: KontaktValues) => {
	const advokatPath = `${path}.advokatSomKontakt`
	const organisasjonPath = `${path}.organisasjonSomKontakt`
	const personPath = `${path}.personSomKontakt`

	const getKontakttype = () => {
		const kontaktType = _.get(formikBag.values, `${path}.kontaktType`)
		if (kontaktType) {
			return kontaktType
		} else if (_.get(formikBag.values, `${path}.advokatSomKontakt`)) {
			return 'ADVOKAT'
		} else if (_.get(formikBag.values, `${path}.organisasjonSomKontakt`)) {
			return 'ORGANISASJON'
		} else if (
			eksisterendeNyPerson ||
			_.get(formikBag.values, `${path}.personSomKontakt.identifikasjonsnummer`) ||
			_.get(formikBag.values, `${path}.personSomKontakt.foedselsdato`)
		) {
			return 'PERSON_FDATO'
		} else if (_.get(formikBag.values, `${path}.personSomKontakt.nyKontaktperson`)) {
			return 'NY_PERSON'
		} else return null
	}

	const { navnInfo, loading } = useGenererNavn()
	const navnOptions = SelectOptionsFormat.formatOptions('personnavn', navnInfo)

	useEffect(() => {
		if (!_.get(formikBag.values, `${path}.kontaktType`)) {
			formikBag.setFieldValue(`${path}.kontaktType`, getKontakttype())
		}
	}, [])

	const handleAfterChange = (type: TypeValues) => {
		const { value } = type
		const kontaktinfo = _.get(formikBag.values, path)
		const kontaktinfoClone = _.cloneDeep(kontaktinfo)

		if (value !== getKontakttype()) {
			_.set(kontaktinfoClone, 'kontaktType', value)
			if (value === 'ADVOKAT') {
				_.set(kontaktinfoClone, 'advokatSomKontakt', initialOrganisasjon)
				_.set(kontaktinfoClone, 'organisasjonSomKontakt', undefined)
				_.set(kontaktinfoClone, 'personSomKontakt', undefined)
			} else if (value === 'ORGANISASJON') {
				_.set(kontaktinfoClone, 'organisasjonSomKontakt', initialOrganisasjon)
				_.set(kontaktinfoClone, 'advokatSomKontakt', undefined)
				_.set(kontaktinfoClone, 'personSomKontakt', undefined)
			} else if (value === 'PERSON_FDATO') {
				_.set(kontaktinfoClone, 'personSomKontakt', initialPerson)
				_.set(kontaktinfoClone, 'advokatSomKontakt', undefined)
				_.set(kontaktinfoClone, 'organisasjonSomKontakt', undefined)
			} else if (value === 'NY_PERSON') {
				_.set(kontaktinfoClone, 'personSomKontakt', initialNyPerson)
				_.set(kontaktinfoClone, 'advokatSomKontakt', undefined)
				_.set(kontaktinfoClone, 'organisasjonSomKontakt', undefined)
			}
		}
		formikBag.setFieldValue(path, kontaktinfoClone)
	}

	const organisasjonHandleChange = (values: OrgValues, orgPath: string) => {
		formikBag.setFieldValue(`${orgPath}.organisasjonsnummer`, values.orgnr)
		formikBag.setFieldValue(`${orgPath}.organisasjonsnavn`, values.navn)
	}

	const disableIdent =
		_.get(formikBag.values, `${personPath}.foedselsdato`) ||
		_.get(formikBag.values, `${personPath}.navn.fornavn`)

	const disablePersoninfo = _.get(formikBag.values, `${personPath}.identifikasjonsnummer`)

	return (
		<Kategori title="Kontakt">
			<FormikSelect
				name={`${path}.kontaktType`}
				label="Kontakttype"
				value={getKontakttype()}
				options={Options('kontaktType')}
				onChange={handleAfterChange}
				isClearable={false}
				size="large"
			/>
			{getKontakttype() === 'ADVOKAT' && (
				<div className="flexbox--flex-wrap">
					<OrganisasjonSelect
						path={`${advokatPath}.organisasjonsnummer`}
						label="Organisasjonsnummer"
						afterChange={(val: OrgValues) => organisasjonHandleChange(val, advokatPath)}
					/>
					<DollySelect
						name={`${advokatPath}.kontaktperson.fornavn`}
						label="Kontaktperson navn"
						options={navnOptions}
						size="large"
						placeholder={getPlaceholder(formikBag.values, `${advokatPath}.kontaktperson`)}
						isLoading={loading}
						onChange={(navn: string) =>
							setNavn(navn, `${advokatPath}.kontaktperson`, formikBag.setFieldValue)
						}
						value={_.get(formikBag.values, `${advokatPath}.kontaktperson.fornavn`)}
					/>
				</div>
			)}

			{getKontakttype() === 'ORGANISASJON' && (
				<div className="flexbox--flex-wrap">
					<OrganisasjonSelect
						path={`${organisasjonPath}.organisasjonsnummer`}
						afterChange={(val: OrgValues) => organisasjonHandleChange(val, organisasjonPath)}
					/>
					<DollySelect
						name={`${organisasjonPath}.kontaktperson.fornavn`}
						label="Kontaktperson navn"
						options={navnOptions}
						size="large"
						placeholder={getPlaceholder(formikBag.values, `${organisasjonPath}.kontaktperson`)}
						isLoading={loading}
						onChange={(navn: string) =>
							setNavn(navn, `${organisasjonPath}.kontaktperson`, formikBag.setFieldValue)
						}
						value={_.get(formikBag.values, `${organisasjonPath}.kontaktperson.fornavn`)}
					/>
				</div>
			)}

			{getKontakttype() === 'PERSON_FDATO' && (
				<div className="flexbox--flex-wrap">
					<PdlEksisterendePerson
						eksisterendePersonPath={`${personPath}.identifikasjonsnummer`}
						label="Kontaktperson"
						disabled={disableIdent}
						eksisterendeNyPerson={eksisterendeNyPerson}
						formMethods={formMethods}
					/>
					<DatepickerWrapper>
						<FormikDatepicker
							name={`${personPath}.foedselsdato`}
							label="Fødselsdato"
							disabled={disablePersoninfo}
							maxDate={new Date()}
							fastfield={false}
						/>
					</DatepickerWrapper>
					<DollySelect
						name={`${personPath}.navn.fornavn`}
						label="Kontaktperson navn"
						options={navnOptions}
						size="xlarge"
						placeholder={getPlaceholder(formikBag.values, `${personPath}.navn`)}
						isLoading={loading}
						onChange={(navn: string) =>
							setNavn(navn, `${personPath}.navn`, formikBag.setFieldValue)
						}
						value={_.get(formikBag.values, `${personPath}.navn.fornavn`)}
						isDisabled={disablePersoninfo}
					/>
				</div>
			)}

			{getKontakttype() === 'NY_PERSON' && (
				<PdlNyPerson nyPersonPath={`${personPath}.nyKontaktperson`} formMethods={formMethods} />
			)}
		</Kategori>
	)
}

import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getPlaceholder, setNavn } from '../utils'
import * as _ from 'lodash-es'
import {
	initialNyPerson,
	initialOrganisasjon,
	initialPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { OrganisasjonSelect } from '@/components/organisasjonSelect'
import { PdlNyPerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { useContext, useEffect } from 'react'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { UseFormReturn } from 'react-hook-form/dist/types'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

interface KontaktValues {
	formMethods: UseFormReturn
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
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const advokatPath = `${path}.advokatSomKontakt`
	const organisasjonPath = `${path}.organisasjonSomKontakt`
	const personPath = `${path}.personSomKontakt`

	const getKontakttype = () => {
		const kontaktType = formMethods.watch(`${path}.kontaktType`)
		if (kontaktType) {
			return kontaktType
		} else if (formMethods.watch(`${path}.advokatSomKontakt`)) {
			return 'ADVOKAT'
		} else if (formMethods.watch(`${path}.organisasjonSomKontakt`)) {
			return 'ORGANISASJON'
		} else if (
			eksisterendeNyPerson ||
			formMethods.watch(`${path}.personSomKontakt.identifikasjonsnummer`) ||
			formMethods.watch(`${path}.personSomKontakt.foedselsdato`)
		) {
			return 'PERSON_FDATO'
		} else if (formMethods.watch(`${path}.personSomKontakt.nyKontaktperson`)) {
			return 'NY_PERSON'
		} else return null
	}

	const { navnInfo, loading } = useGenererNavn()
	const navnOptions = SelectOptionsFormat.formatOptions('personnavn', navnInfo)

	useEffect(() => {
		if (!formMethods.watch(`${path}.kontaktType`)) {
			formMethods.setValue(`${path}.kontaktType`, getKontakttype())
			formMethods.trigger(`${path}.kontaktType`)
		}
	}, [])

	const handleAfterChange = (type: TypeValues) => {
		const { value } = type
		const kontaktinfo = formMethods.watch(path)
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
		formMethods.setValue(path, kontaktinfoClone)
		formMethods.trigger(path)
	}

	const organisasjonHandleChange = (values: OrgValues, orgPath: string) => {
		formMethods.setValue(`${orgPath}.organisasjonsnummer`, values.orgnr)
		formMethods.setValue(`${orgPath}.organisasjonsnavn`, values.navn)
		formMethods.trigger(orgPath)
	}

	const disableIdent =
		formMethods.watch(`${personPath}.foedselsdato`) ||
		formMethods.watch(`${personPath}.navn.fornavn`)

	const disablePersoninfo = formMethods.watch(`${personPath}.identifikasjonsnummer`)

	const kontakter = Options('kontaktType').filter(
		(type) => type.value !== 'PERSON_FDATO' || opts?.antall === 1,
	)
	return (
		<Kategori title="Kontakt">
			<FormSelect
				name={`${path}.kontaktType`}
				label="Kontakttype"
				value={getKontakttype()}
				options={kontakter}
				onChange={handleAfterChange}
				isClearable={false}
				size="large"
				info={
					opts?.antall > 1 &&
					'"Person med identifikasjon" er tilgjengelig for individ, ikke for gruppe'
				}
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
						placeholder={getPlaceholder(formMethods.getValues(), `${advokatPath}.kontaktperson`)}
						isLoading={loading}
						onChange={(navn: string) =>
							setNavn(navn, `${advokatPath}.kontaktperson`, formMethods.setValue)
						}
						value={formMethods.watch(`${advokatPath}.kontaktperson.fornavn`)}
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
						placeholder={getPlaceholder(
							formMethods.getValues(),
							`${organisasjonPath}.kontaktperson`,
						)}
						isLoading={loading}
						onChange={(navn: string) =>
							setNavn(navn, `${organisasjonPath}.kontaktperson`, formMethods.setValue)
						}
						value={formMethods.watch(`${organisasjonPath}.kontaktperson.fornavn`)}
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
					<FormDatepicker
						name={`${personPath}.foedselsdato`}
						label="Fødselsdato"
						disabled={disablePersoninfo}
						maxDate={new Date()}
					/>
					<DollySelect
						name={`${personPath}.navn.fornavn`}
						label="Kontaktperson navn"
						options={navnOptions}
						size="xlarge"
						placeholder={getPlaceholder(formMethods.getValues(), `${personPath}.navn`)}
						isLoading={loading}
						onChange={(navn: string) => {
							setNavn(navn, `${personPath}.navn`, formMethods.setValue)
							formMethods.setValue(`${personPath}.identifikasjonsnummer`, undefined)
							formMethods.trigger(personPath)
						}}
						value={formMethods.watch(`${personPath}.navn.fornavn`)}
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

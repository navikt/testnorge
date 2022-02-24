import React, { useContext, useEffect, useState } from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { Option, SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { getPlaceholder, setNavn } from '../utils'
import _get from 'lodash/get'
import {
	initialNyPerson,
	initialOrganisasjon,
	initialPerson,
} from '~/components/fagsystem/pdlf/form/initialValues'
import { OrganisasjonSelect } from '~/components/organisasjonSelect'
import _cloneDeep from 'lodash/cloneDeep'
import _set from 'lodash/set'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { FormikProps } from 'formik'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { identFraTestnorge } from '~/components/bestillingsveileder/stegVelger/steg/steg1/Steg1Person'
import { useBoolean } from 'react-use'
import Loading from '~/components/ui/loading/Loading'

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

export const Kontakt = ({ formikBag, path }: KontaktValues) => {
	const opts = useContext(BestillingsveilederContext)
	const { gruppeId } = opts
	const isTestnorgeIdent = identFraTestnorge(opts)

	const [identOptions, setIdentOptions] = useState<Array<Option>>([])
	const [loadingIdentOptions, setLoadingIdentOptions] = useBoolean(true)

	useEffect(() => {
		if (!isTestnorgeIdent) {
			const eksisterendeIdent = opts.personFoerLeggTil?.pdlforvalter?.person?.ident
			SelectOptionsOppslag.hentGruppeIdentOptions(gruppeId).then((response: [Option]) => {
				setIdentOptions(response?.filter((person) => person.value !== eksisterendeIdent))
				setLoadingIdentOptions(false)
			})
		}
	}, [])

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

	const organisasjonHandleChange = (values: OrgValues, orgPath: string) => {
		formikBag.setFieldValue(`${orgPath}.organisasjonsnummer`, values.orgnr)
		formikBag.setFieldValue(`${orgPath}.organisasjonsnavn`, values.navn)
	}

	const disableIdent =
		_get(formikBag.values, `${personPath}.foedselsdato`) ||
		_get(formikBag.values, `${personPath}.navn.fornavn`)

	const disablePersoninfo = _get(formikBag.values, `${personPath}.identifikasjonsnummer`)

	return (
		<Kategori title="Kontakt">
			<FormikSelect
				name={`${path}.kontaktType`}
				label="Kontakttype"
				value={kontaktType}
				options={Options('kontaktType')}
				onChange={handleAfterChange}
				isClearable={false}
				size="large"
			/>
			{kontaktType === 'ADVOKAT' && (
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
						size="xlarge"
						placeholder={getPlaceholder(formikBag.values, `${advokatPath}.kontaktperson`)}
						isLoading={navnInfo.loading}
						onChange={(navn: string) =>
							setNavn(navn, `${advokatPath}.kontaktperson`, formikBag.setFieldValue)
						}
						value={_get(formikBag.values, `${advokatPath}.kontaktperson.fornavn`)}
					/>
				</div>
			)}

			{kontaktType === 'ORGANISASJON' && (
				<div className="flexbox--flex-wrap">
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
				</div>
			)}

			{kontaktType === 'PERSON_FDATO' && (
				<div className="flexbox--flex-wrap">
					{loadingIdentOptions && <Loading label="Henter valg for eksisterende ident..." />}
					{identOptions?.length > 0 && (
						<FormikSelect
							name={`${personPath}.identifikasjonsnummer`}
							label="Kontaktperson"
							options={identOptions}
							size={'xlarge'}
							disabled={disableIdent}
						/>
					)}
					<FormikDatepicker
						name={`${personPath}.foedselsdato`}
						label="Fødselsdato"
						disabled={disablePersoninfo}
						fastfield={false}
					/>
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
						disabled={disablePersoninfo}
					/>
				</div>
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

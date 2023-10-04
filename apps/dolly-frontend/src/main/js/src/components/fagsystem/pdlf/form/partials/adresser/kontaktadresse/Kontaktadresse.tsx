import React, { useEffect } from 'react'
import * as _ from 'lodash-es'
import {
	initialKontaktadresse,
	initialPostboksadresse,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import {
	VegadresseVelger,
	UtenlandskAdresse,
	Postboksadresse,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikProps } from 'formik'
import { Adressetype } from '@/components/fagsystem/pdlf/PdlTypes'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { getPlaceholder, setNavn } from '@/components/fagsystem/pdlf/form/partials/utils'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import {useGenererNavn} from "@/utils/hooks/useGenererNavn";

interface KontaktadresseValues {
	formikBag: FormikProps<{}>
}

type KontaktadresseFormValues = {
	formikBag: FormikProps<{}>
	path: string
	idx?: number
}

type Target = {
	value: string
	label?: string
}

export const KontaktadresseForm = ({ formikBag, path, idx }: KontaktadresseFormValues) => {
	useEffect(() => {
		formikBag.setFieldValue(`${path}.adresseIdentifikatorFraMatrikkelen`, undefined)
		const kontaktadresse = _.get(formikBag.values, path)
		if (_.get(kontaktadresse, 'vegadresse') && _.get(kontaktadresse, 'vegadresse') !== null) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Veg)
		} else if (
			_.get(kontaktadresse, 'utenlandskAdresse') &&
			_.get(kontaktadresse, 'utenlandskAdresse') !== null
		) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Utenlandsk)
		} else if (
			_.get(kontaktadresse, 'postboksadresse') &&
			_.get(kontaktadresse, 'postboksadresse') !== null
		) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Postboks)
		}
	}, [])

	const valgtAdressetype = _.get(formikBag.values, `${path}.adressetype`)

	const handleChangeAdressetype = (target: Target, path: string) => {
		const adresse = _.get(formikBag.values, path)
		const adresseClone = _.cloneDeep(adresse)

		_.set(adresseClone, 'adressetype', target?.value || null)

		if (!target) {
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'VEGADRESSE') {
			_.set(adresseClone, 'vegadresse', initialVegadresse)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_.set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'POSTBOKSADRESSE') {
			_.set(adresseClone, 'postboksadresse', initialPostboksadresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
		}

		formikBag.setFieldValue(path, adresseClone)
	}

	const {navnInfo} = useGenererNavn()
	const navnOptions = SelectOptionsOppslag.formatOptions('personnavn', navnInfo)

	return (
		<React.Fragment key={idx}>
			<div className="flexbox--full-width">
				<FormikSelect
					name={`${path}.adressetype`}
					label="Adressetype"
					options={Options('adressetypeKontaktadresse')}
					onChange={(target: Target) => handleChangeAdressetype(target, path)}
					size="large"
				/>
			</div>
			{valgtAdressetype === 'VEGADRESSE' && (
				<VegadresseVelger formikBag={formikBag} path={`${path}.vegadresse`} key={`veg_${idx}`} />
			)}
			{valgtAdressetype === 'UTENLANDSK_ADRESSE' && (
				<UtenlandskAdresse
					formikBag={formikBag}
					path={`${path}.utenlandskAdresse`}
					master={_.get(formikBag.values, `${path}.master`)}
				/>
			)}
			{valgtAdressetype === 'POSTBOKSADRESSE' && (
				<Postboksadresse formikBag={formikBag} path={`${path}.postboksadresse`} />
			)}
			<div className="flexbox--flex-wrap">
				<DatepickerWrapper>
					<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." />
					<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig t.o.m." />
				</DatepickerWrapper>
				<DollySelect
					name={`${path}.opprettCoAdresseNavn.fornavn`}
					label="C/O adressenavn"
					options={navnOptions}
					size="xlarge"
					placeholder={getPlaceholder(formikBag.values, `${path}.opprettCoAdresseNavn`)}
					isLoading={navnInfo.loading}
					onChange={(navn: Target) =>
						setNavn(navn, `${path}.opprettCoAdresseNavn`, formikBag.setFieldValue)
					}
					value={_.get(formikBag.values, `${path}.opprettCoAdresseNavn.fornavn`)}
				/>
			</div>
			<AvansertForm path={path} />
		</React.Fragment>
	)
}

export const Kontaktadresse = ({ formikBag }: KontaktadresseValues) => {
	return (
		<Kategori title="Kontaktadresse">
			<FormikDollyFieldArray
				name="pdldata.person.kontaktadresse"
				header="Kontaktadresse"
				newEntry={initialKontaktadresse}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<KontaktadresseForm formikBag={formikBag} path={path} idx={idx} />
				)}
			</FormikDollyFieldArray>
		</Kategori>
	)
}

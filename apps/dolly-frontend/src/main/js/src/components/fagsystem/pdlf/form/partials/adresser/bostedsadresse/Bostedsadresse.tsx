import React, { useContext, useEffect } from 'react'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import {
	initialBostedsadresse,
	initialMatrikkeladresse,
	initialUkjentBosted,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as _ from 'lodash-es'
import {
	UtenlandskAdresse,
	UkjentBosted,
	VegadresseVelger,
	MatrikkeladresseVelger,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { FormikProps } from 'formik'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/Bestillingsveileder'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { Adressetype } from '@/components/fagsystem/pdlf/PdlTypes'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { getPlaceholder, setNavn } from '@/components/fagsystem/pdlf/form/partials/utils'

interface BostedsadresseValues {
	formikBag: FormikProps<{}>
}

type BostedsadresseFormValues = {
	formikBag: FormikProps<{}>
	path: string
	idx?: number
	identtype?: string
}

type Target = {
	value: string
	label?: string
}

export const BostedsadresseForm = ({
	formikBag,
	path,
	idx,
	identtype,
}: BostedsadresseFormValues) => {
	useEffect(() => {
		formikBag.setFieldValue(`${path}.adresseIdentifikatorFraMatrikkelen`, undefined)
		const boadresse = _.get(formikBag.values, path)
		if (_.get(boadresse, 'vegadresse') && _.get(boadresse, 'vegadresse') !== null) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Veg)
		} else if (
			_.get(boadresse, 'matrikkeladresse') &&
			_.get(boadresse, 'matrikkeladresse') !== null
		) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Matrikkel)
		} else if (
			_.get(boadresse, 'utenlandskAdresse') &&
			_.get(boadresse, 'utenlandskAdresse') !== null
		) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Utenlandsk)
		} else if (_.get(boadresse, 'ukjentBosted') && _.get(boadresse, 'ukjentBosted') !== null) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Ukjent)
		}
	}, [])

	const valgtAdressetype = _.get(formikBag.values, `${path}.adressetype`)

	const opts = useContext(BestillingsveilederContext)
	const getAdresseOptions = () => {
		if ((opts?.identtype && opts?.identtype !== 'FNR') || (identtype && identtype !== 'FNR')) {
			return Options('adressetypeUtenlandskBostedsadresse')
		}
		return Options('adressetypeBostedsadresse')
	}

	const handleChangeAdressetype = (target: Target, path: string) => {
		const adresse = _.get(formikBag.values, path)
		const adresseClone = _.cloneDeep(adresse)

		_.set(adresseClone, 'adressetype', target?.value || null)

		if (!target) {
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
		}
		if (target?.value === 'VEGADRESSE') {
			_.set(adresseClone, 'vegadresse', initialVegadresse)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
			_.set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'MATRIKKELADRESSE') {
			_.set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
			_.set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_.set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
			_.set(adresseClone, 'master', 'PDL')
		}
		if (target?.value === 'UKJENT_BOSTED') {
			_.set(adresseClone, 'ukjentBosted', initialUkjentBosted)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'master', 'FREG')
		}

		formikBag.setFieldValue(path, adresseClone)
	}

	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	const navnOptions = SelectOptionsOppslag.formatOptions('personnavn', navnInfo)

	return (
		<React.Fragment key={idx}>
			<div className="flexbox--full-width">
				<FormikSelect
					name={`${path}.adressetype`}
					label="Adressetype"
					options={getAdresseOptions()}
					onChange={(target: Target) => handleChangeAdressetype(target, path)}
					size="large"
				/>
			</div>
			{valgtAdressetype === 'VEGADRESSE' && (
				<VegadresseVelger formikBag={formikBag} path={`${path}.vegadresse`} key={`veg_${idx}`} />
			)}
			{valgtAdressetype === 'MATRIKKELADRESSE' && (
				<MatrikkeladresseVelger formikBag={formikBag} path={`${path}.matrikkeladresse`} />
			)}
			{valgtAdressetype === 'UTENLANDSK_ADRESSE' && (
				<UtenlandskAdresse formikBag={formikBag} path={`${path}.utenlandskAdresse`} />
			)}
			{valgtAdressetype === 'UKJENT_BOSTED' && (
				<UkjentBosted formikBag={formikBag} path={`${path}.ukjentBosted`} />
			)}
			<div className="flexbox--flex-wrap">
				<DatepickerWrapper>
					<FormikDatepicker name={`${path}.angittFlyttedato`} label="Flyttedato" />
					<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." addHour />
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
			<AvansertForm path={path} kanVelgeMaster={valgtAdressetype === null} />
		</React.Fragment>
	)
}

export const Bostedsadresse = ({ formikBag }: BostedsadresseValues) => {
	return (
		<Kategori title="Bostedsadresse">
			<FormikDollyFieldArray
				name="pdldata.person.bostedsadresse"
				header="Bostedsadresse"
				newEntry={initialBostedsadresse}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<BostedsadresseForm formikBag={formikBag} path={path} idx={idx} />
				)}
			</FormikDollyFieldArray>
		</Kategori>
	)
}

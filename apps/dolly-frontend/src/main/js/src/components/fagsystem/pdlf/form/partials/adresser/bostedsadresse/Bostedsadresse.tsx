import React, { useContext, useEffect } from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import {
	initialBostedsadresse,
	initialMatrikkeladresse,
	initialUkjentBosted,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import _get from 'lodash/get'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/UtenlandskAdresse'
import _cloneDeep from 'lodash/cloneDeep'
import _set from 'lodash/set'
import { UkjentBosted } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/UkjentBosted'
import { VegadresseVelger } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/VegadresseVelger'
import { MatrikkeladresseVelger } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/MatrikkeladresseVelger'
import { FormikProps } from 'formik'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

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
}

enum Adressetype {
	Veg = 'VEGADRESSE',
	Matrikkel = 'MATRIKKELADRESSE',
	Utenlandsk = 'UTENLANDSK_ADRESSE',
	Ukjent = 'UKJENT_BOSTED',
}

export const BostedsadresseForm = ({
	formikBag,
	path,
	idx,
	identtype,
}: BostedsadresseFormValues) => {
	useEffect(() => {
		formikBag.setFieldValue(`${path}.adresseIdentifikatorFraMatrikkelen`, undefined)
		const boadresse = _get(formikBag.values, path)
		if (_get(boadresse, 'vegadresse') && _get(boadresse, 'vegadresse') !== null) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Veg)
		} else if (
			_get(boadresse, 'matrikkeladresse') &&
			_get(boadresse, 'matrikkeladresse') !== null
		) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Matrikkel)
		} else if (
			_get(boadresse, 'utenlandskAdresse') &&
			_get(boadresse, 'utenlandskAdresse') !== null
		) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Utenlandsk)
		} else if (_get(boadresse, 'ukjentBosted') && _get(boadresse, 'ukjentBosted') !== null) {
			formikBag.setFieldValue(`${path}.adressetype`, Adressetype.Ukjent)
		}
	}, [])

	const valgtAdressetype = _get(formikBag.values, `${path}.adressetype`)

	const opts = useContext(BestillingsveilederContext)
	const getAdresseOptions = () => {
		if ((opts?.identtype && opts?.identtype !== 'FNR') || (identtype && identtype !== 'FNR')) {
			return Options('adressetypeUtenlandskBostedsadresse')
		}
		return Options('adressetypeBostedsadresse')
	}

	const handleChangeAdressetype = (target: Target, path: string) => {
		const adresse = _get(formikBag.values, path)
		const adresseClone = _cloneDeep(adresse)

		_set(adresseClone, 'adressetype', target?.value || null)

		if (!target) {
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'ukjentBosted', undefined)
		}
		if (target?.value === 'VEGADRESSE') {
			_set(adresseClone, 'vegadresse', initialVegadresse)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'ukjentBosted', undefined)
			_set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'MATRIKKELADRESSE') {
			_set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'ukjentBosted', undefined)
			_set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'ukjentBosted', undefined)
			_set(adresseClone, 'master', 'PDL')
		}
		if (target?.value === 'UKJENT_BOSTED') {
			_set(adresseClone, 'ukjentBosted', initialUkjentBosted)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'master', 'FREG')
		}

		formikBag.setFieldValue(path, adresseClone)
	}

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
				<FormikDatepicker name={`${path}.angittFlyttedato`} label="Flyttedato" />
				<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." />
				<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig t.o.m." />
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

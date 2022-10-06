import React, { useState } from 'react'
import _get from 'lodash/get'
import _set from 'lodash/set'
import _cloneDeep from 'lodash/cloneDeep'
import { FormikProps } from 'formik'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import {
	VegadresseVelger,
	MatrikkeladresseVelger,
	UkjentBosted,
} from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import {
	initialMatrikkeladresse,
	initialUkjentBosted,
	initialVegadresse,
} from '~/components/fagsystem/pdlf/form/initialValues'

interface DeltBostedValues {
	formikBag: FormikProps<{}>
	path: string
}

type Target = {
	value: string
}

export const DeltBosted = ({ formikBag, path }: DeltBostedValues) => {
	const [adressetype, setAdressetype] = useState(_get(formikBag.values, `${path}.adressetype`))

	const handleChangeAdressetype = (target: Target, adressePath: string) => {
		const adresse = _get(formikBag.values, adressePath)
		const adresseClone = _cloneDeep(adresse)

		if (!target || target?.value === 'PARTNER_ADRESSE') {
			_set(adresseClone, 'vegadresse', null)
			_set(adresseClone, 'matrikkeladresse', null)
			_set(adresseClone, 'ukjentBosted', null)
		} else if (target?.value === 'VEGADRESSE') {
			_set(adresseClone, 'vegadresse', initialVegadresse)
			_set(adresseClone, 'matrikkeladresse', null)
			_set(adresseClone, 'ukjentBosted', null)
		} else if (target?.value === 'MATRIKKELADRESSE') {
			_set(adresseClone, 'vegadresse', null)
			_set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_set(adresseClone, 'ukjentBosted', null)
		} else if (target?.value === 'UKJENT_BOSTED') {
			_set(adresseClone, 'vegadresse', null)
			_set(adresseClone, 'matrikkeladresse', null)
			_set(adresseClone, 'ukjentBosted', initialUkjentBosted)
		}

		setAdressetype(target?.value)
		_set(adresseClone, 'adressetype', target?.value || null)
		formikBag.setFieldValue(path, adresseClone)
	}
	return (
		<Kategori title="Delt bosted">
			<FormikSelect
				name={`${path}.adressetype`}
				value={adressetype}
				label="Adressetype"
				options={Options('adressetypeDeltBosted')}
				onChange={(target: Target) => handleChangeAdressetype(target, path)}
				size="large"
			/>

			{adressetype === 'VEGADRESSE' && (
				<VegadresseVelger formikBag={formikBag} path={`${path}.vegadresse`} />
			)}
			{adressetype === 'MATRIKKELADRESSE' && (
				<MatrikkeladresseVelger formikBag={formikBag} path={`${path}.matrikkeladresse`} />
			)}
			{adressetype === 'UKJENT_BOSTED' && (
				<UkjentBosted formikBag={formikBag} path={`${path}.ukjentBosted`} />
			)}
			<div className="flexbox--flex-wrap">
				<FormikDatepicker name={`${path}.startdatoForKontrakt`} label="Startdato for kontrakt" />
				<FormikDatepicker name={`${path}.sluttdatoForKontrakt`} label="Sluttdato for kontrakt" />
			</div>
		</Kategori>
	)
}

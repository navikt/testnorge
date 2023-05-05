import React, { useState } from 'react'
import * as _ from 'lodash-es'
import { FormikProps } from 'formik'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import {
	VegadresseVelger,
	MatrikkeladresseVelger,
	UkjentBosted,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import {
	initialMatrikkeladresse,
	initialUkjentBosted,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'

interface DeltBostedValues {
	formikBag: FormikProps<{}>
	path: string
}

type Target = {
	value: string
}

export const DeltBostedForm = ({ formikBag, path }: DeltBostedValues) => {
	const getAdressetype = () => {
		const type = _.get(formikBag.values, `${path}.adressetype`)
		//TODO: Sjekk for adresse fra partner?
		if (type) {
			return type
		} else if (_.get(formikBag.values, `${path}.vegadresse`)) {
			return 'VEGADRESSE'
		} else if (_.get(formikBag.values, `${path}.matrikkeladresse`)) {
			return 'MATRIKKELADRESSE'
		} else if (_.get(formikBag.values, `${path}.ukjentBosted`)) {
			return 'UKJENT_BOSTED'
		}
	}

	const [adressetype, setAdressetype] = useState(getAdressetype())

	const handleChangeAdressetype = (target: Target, adressePath: string) => {
		const adresse = _.get(formikBag.values, adressePath)
		const adresseClone = _.cloneDeep(adresse)

		if (!target || target?.value === 'PARTNER_ADRESSE') {
			_.set(adresseClone, 'vegadresse', null)
			_.set(adresseClone, 'matrikkeladresse', null)
			_.set(adresseClone, 'ukjentBosted', null)
		} else if (target?.value === 'VEGADRESSE') {
			_.set(adresseClone, 'vegadresse', initialVegadresse)
			_.set(adresseClone, 'matrikkeladresse', null)
			_.set(adresseClone, 'ukjentBosted', null)
		} else if (target?.value === 'MATRIKKELADRESSE') {
			_.set(adresseClone, 'vegadresse', null)
			_.set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_.set(adresseClone, 'ukjentBosted', null)
		} else if (target?.value === 'UKJENT_BOSTED') {
			_.set(adresseClone, 'vegadresse', null)
			_.set(adresseClone, 'matrikkeladresse', null)
			_.set(adresseClone, 'ukjentBosted', initialUkjentBosted)
		}

		setAdressetype(target?.value)
		_.set(adresseClone, 'adressetype', target?.value || null)
		formikBag.setFieldValue(path, adresseClone)
	}
	console.log('formikBag.values: ', formikBag.values) //TODO - SLETT MEG
	console.log('formikBag.errors: ', formikBag.errors) //TODO - SLETT MEG
	console.log('adressetype: ', adressetype) //TODO - SLETT MEG
	return (
		<>
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
		</>
	)
}

export const DeltBosted = ({ formikBag, path }: DeltBostedValues) => {
	return (
		<Kategori title="Delt bosted">
			<DeltBostedForm formikBag={formikBag} path={path} />
		</Kategori>
	)
}

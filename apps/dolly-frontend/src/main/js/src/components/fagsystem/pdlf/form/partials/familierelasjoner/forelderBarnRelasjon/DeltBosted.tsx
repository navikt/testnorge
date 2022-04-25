import React, { useContext, useState } from 'react'
import _get from 'lodash/get'
import _set from 'lodash/set'
import _cloneDeep from 'lodash/cloneDeep'
import { FormikProps } from 'formik'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { VegadresseVelger } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/VegadresseVelger'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { MatrikkeladresseVelger } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/MatrikkeladresseVelger'
import { UkjentBosted } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/UkjentBosted'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import {
	initialMatrikkeladresse,
	initialUkjentBosted,
	initialVegadresse,
} from '~/components/fagsystem/pdlf/form/initialValues'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

interface DeltBostedValues {
	formikBag: FormikProps<{}>
	path: string
}

type Target = {
	value: string
}

export const DeltBosted = ({ formikBag, path }: DeltBostedValues) => {
	const [adressetype, setAdressetype] = useState(null)
	const [error, setError] = useState('Må velge adressetype')

	const feilMelding = (values: any, type: string) => {
		if (type === null) return 'Må velge adressetype'
		if (type === 'PARTNER_ADRESSE') {
			if (values?.pdldata?.person?.sivilstand?.[0]?.borIkkeSammen) {
				return null
			}
			return 'Fant ikke gyldig partner for delt bosted'
		}

		return null
	}

	const handleChangeAdressetype = (target: Target, path: string) => {
		const adresse = _get(formikBag.values, path)
		const adresseClone = _cloneDeep(adresse)

		const feil = feilMelding(formikBag.values, target?.value)
		setError(feil)

		if (!target || feil || target?.value === 'PARTNER_ADRESSE') {
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'ukjentBosted', undefined)
		} else if (target?.value === 'VEGADRESSE') {
			_set(adresseClone, 'vegadresse', initialVegadresse)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'ukjentBosted', undefined)
		} else if (target?.value === 'MATRIKKELADRESSE') {
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_set(adresseClone, 'ukjentBosted', undefined)
		} else if (target?.value === 'UKJENT_BOSTED') {
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'ukjentBosted', initialUkjentBosted)
		}

		setAdressetype(target?.value)
		_set(adresseClone, 'adressetype', feil ? null : target?.value || null)
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
				feil={
					error && {
						feilmelding: error,
					}
				}
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

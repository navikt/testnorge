import React, { useEffect, useState } from 'react'
import * as _ from 'lodash-es'
import { FormikProps } from 'formik'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import {
	MatrikkeladresseVelger,
	UkjentBosted,
	VegadresseVelger,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import {
	initialMatrikkeladresse,
	initialUkjentBosted,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'

interface DeltBostedValues {
	formikBag: FormikProps<{}>
	path: string
}

type Target = {
	value: string
}

const endreAdressetypeBosted = (forelderBarnRelasjoner) => {
	if (!forelderBarnRelasjoner) {
		return null
	}

	let options = [
		{ value: 'VEGADRESSE', label: 'Vegadresse' },
		{ value: 'MATRIKKELADRESSE', label: 'Matrikkeladresse' },
		{ value: 'UKJENT_BOSTED', label: 'Ukjent bosted' },
	]

	const foreldrerelasjoner = forelderBarnRelasjoner?.filter((a) => {
		return a && a.relatertPersonsRolle !== 'BARN'
	})

	foreldrerelasjoner.forEach((forelder) => {
		options.unshift({
			value: forelder?.relatertPerson,
			label: `Adresse fra ${forelder?.relatertPersonsRolle?.toLowerCase()} (${forelder?.relatertPerson})`,
		})
	})

	return options
}

export const DeltBostedForm = ({
	formMethods,
	path,
	relasjoner,
	personValues,
}: DeltBostedValues) => {
	const getAdressetype = () => {
		const type = _.get(formMethods.getValues(), `${path}.adressetype`)
		if (type) {
			return type
		} else if (_.get(formMethods.getValues(), `${path}.vegadresse`)) {
			return 'VEGADRESSE'
		} else if (_.get(formMethods.getValues(), `${path}.matrikkeladresse`)) {
			return 'MATRIKKELADRESSE'
		} else if (_.get(formMethods.getValues(), `${path}.ukjentBosted`)) {
			return 'UKJENT_BOSTED'
		}
	}

	const [adressetype, setAdressetype] = useState(getAdressetype())

	useEffect(() => {
		if (!_.get(formMethods.getValues(), `${path}.adressetype`)) {
			formMethods.setValue(`${path}.adressetype`, getAdressetype())
		}
	}, [])

	const handleChangeAdressetype = (target: Target, adressePath: string) => {
		const adresse = _.get(formMethods.getValues(), adressePath)
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
		} else if (target?.value && relasjoner?.length > 0) {
			const foreldersAdresse = relasjoner.find(
				(forelder) => forelder?.relatertPerson?.ident == target?.value,
			)?.relatertPerson?.bostedsadresse?.[0]
			if (foreldersAdresse?.vegadresse) {
				_.set(adresseClone, 'vegadresse', foreldersAdresse?.vegadresse)
				_.set(adresseClone, 'matrikkeladresse', null)
				_.set(adresseClone, 'ukjentBosted', null)
			} else if (foreldersAdresse?.matrikkeladresse) {
				_.set(adresseClone, 'vegadresse', null)
				_.set(adresseClone, 'matrikkeladresse', foreldersAdresse?.matrikkeladresse)
				_.set(adresseClone, 'ukjentBosted', null)
			} else if (foreldersAdresse?.ukjentBosted) {
				_.set(adresseClone, 'vegadresse', null)
				_.set(adresseClone, 'matrikkeladresse', null)
				_.set(adresseClone, 'ukjentBosted', foreldersAdresse?.ukjentBosted)
			}
		}

		setAdressetype(target?.value)
		_.set(adresseClone, 'adressetype', target?.value || null)
		formMethods.setValue(path, adresseClone)
	}

	return (
		<>
			<FormikSelect
				name={`${path}.adressetype`}
				value={adressetype}
				label="Adressetype"
				options={
					personValues
						? endreAdressetypeBosted(personValues.forelderBarnRelasjon)
						: Options('adressetypeDeltBosted')
				}
				onChange={(target: Target) => handleChangeAdressetype(target, path)}
				size="large"
				isClearable={false}
			/>

			{adressetype === 'VEGADRESSE' && (
				<VegadresseVelger formMethods={formMethods} path={`${path}.vegadresse`} />
			)}
			{adressetype === 'MATRIKKELADRESSE' && (
				<MatrikkeladresseVelger formMethods={formMethods} path={`${path}.matrikkeladresse`} />
			)}
			{adressetype === 'UKJENT_BOSTED' && (
				<UkjentBosted formMethods={formMethods} path={`${path}.ukjentBosted`} />
			)}
			<div className="flexbox--flex-wrap">
				<DatepickerWrapper>
					<FormikDatepicker name={`${path}.startdatoForKontrakt`} label="Startdato for kontrakt" />
					<FormikDatepicker name={`${path}.sluttdatoForKontrakt`} label="Sluttdato for kontrakt" />
				</DatepickerWrapper>
			</div>
		</>
	)
}

export const DeltBosted = ({ formMethods, path }: DeltBostedValues) => {
	return (
		<Kategori title="Delt bosted">
			<DeltBostedForm formMethods={formMethods} path={path} />
		</Kategori>
	)
}

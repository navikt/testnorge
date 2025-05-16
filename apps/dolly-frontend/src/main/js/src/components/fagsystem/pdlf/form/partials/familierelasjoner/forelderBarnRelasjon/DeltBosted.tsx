import React, { useEffect, useState } from 'react'
import * as _ from 'lodash-es'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import {
	MatrikkeladresseVelger,
	UkjentBosted,
	VegadresseVelger,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { DollyDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import {
	initialMatrikkeladresse,
	initialUkjentBosted,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { UseFormReturn } from 'react-hook-form/dist/types'
import StyledAlert from '@/components/ui/alert/StyledAlert'

interface DeltBostedValues {
	formMethods: UseFormReturn
	path: string
	relasjoner?: any[]
	personValues?: any
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
		const type = formMethods.watch(`${path}.adressetype`)
		if (type) {
			return type
		} else if (formMethods.watch(`${path}.vegadresse`)) {
			return 'VEGADRESSE'
		} else if (formMethods.watch(`${path}.matrikkeladresse`)) {
			return 'MATRIKKELADRESSE'
		} else if (formMethods.watch(`${path}.ukjentBosted`)) {
			return 'UKJENT_BOSTED'
		}
	}

	const [adressetype, setAdressetype] = useState(getAdressetype())

	const harPartner = formMethods.watch('pdldata.person.sivilstand')?.length > 0
	const harForelderBarnRelasjon =
		formMethods.watch('pdldata.person.forelderBarnRelasjon')?.length > 0

	useEffect(() => {
		if (!formMethods.watch(`${path}.adressetype`)) {
			formMethods.setValue(`${path}.adressetype`, getAdressetype())
		}
	}, [])

	const handleChangeAdressetype = (target: Target, adressePath: string) => {
		const adresse = formMethods.watch(adressePath)
		const adresseClone = _.cloneDeep(adresse)
		const targetValue = target?.value

		const updateAddressFields = (addressType: string | null, value: any = null) => {
			_.set(adresseClone, 'vegadresse', addressType === 'vegadresse' ? value : null)
			_.set(adresseClone, 'matrikkeladresse', addressType === 'matrikkeladresse' ? value : null)
			_.set(adresseClone, 'ukjentBosted', addressType === 'ukjentBosted' ? value : null)
		}

		if (!target || targetValue === 'PARTNER_ADRESSE') {
			updateAddressFields(null)
		} else if (targetValue === 'VEGADRESSE') {
			updateAddressFields('vegadresse', initialVegadresse)
		} else if (targetValue === 'MATRIKKELADRESSE') {
			updateAddressFields('matrikkeladresse', initialMatrikkeladresse)
		} else if (targetValue === 'UKJENT_BOSTED') {
			updateAddressFields('ukjentBosted', initialUkjentBosted)
		} else if (targetValue && relasjoner && relasjoner.length > 0) {
			const forelder = relasjoner.find((rel) => rel?.relatertPerson?.ident === targetValue)
			const foreldersAdresse = forelder?.relatertPerson?.bostedsadresse?.[0]

			if (foreldersAdresse?.vegadresse) {
				updateAddressFields('vegadresse', foreldersAdresse.vegadresse)
			} else if (foreldersAdresse?.matrikkeladresse) {
				updateAddressFields('matrikkeladresse', foreldersAdresse.matrikkeladresse)
			} else if (foreldersAdresse?.ukjentBosted) {
				updateAddressFields('ukjentBosted', foreldersAdresse.ukjentBosted)
			}
		}

		setAdressetype(targetValue)
		_.set(adresseClone, 'adressetype', targetValue || null)
		formMethods.setValue(path, adresseClone)
	}

	return (
		<>
			{!harPartner && (
				<StyledAlert variant={'warning'}>
					{
						//TODO: Skrive bedre tilbakemelding
					}
					For at delt bosted skal fungere, må identen ha en gjeldende sivilstand med en annen
					adresse enn hovedperson, dette kan velges i Steg 2 under familierelasjoner panel
				</StyledAlert>
			)}
			{!harForelderBarnRelasjon && (
				<StyledAlert variant={'warning'}>
					{
						//TODO: Skrive bedre tilbakemelding
					}
					For at delt bosted skal fungere, må identen ha en relasjon til et barn, dette kan velges i
					Steg 2 under familierelasjoner panel
				</StyledAlert>
			)}
			<FormSelect
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
			{adressetype === 'UKJENT_BOSTED' && <UkjentBosted path={`${path}.ukjentBosted`} />}
			<div className="flexbox--flex-wrap">
				<DollyDatepicker name={`${path}.startdatoForKontrakt`} label="Startdato for kontrakt" />
				<DollyDatepicker name={`${path}.sluttdatoForKontrakt`} label="Sluttdato for kontrakt" />
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

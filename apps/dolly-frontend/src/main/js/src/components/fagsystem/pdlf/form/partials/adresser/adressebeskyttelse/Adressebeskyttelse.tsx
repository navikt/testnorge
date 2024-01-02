import React, { useContext, useEffect, useState } from 'react'
import { getInitialAdressebeskyttelse } from '@/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import * as _ from 'lodash'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface AdressebeskyttelseValues {
	formMethods: UseFormReturn
}

type AdressebeskyttelseFormValues = {
	formMethods: UseFormReturn
	path: string
	idx?: number
	identtype?: string
}

type Target = {
	label: string
	value: string
}

export const getIdenttype = (formMethods: any, identtype: string) => {
	const nyIdenttype = _.get(formMethods.getValues(), 'pdldata.person.nyident[0].identtype')
	if (nyIdenttype) {
		return nyIdenttype
	} else {
		return identtype
			? identtype
			: _.get(formMethods.getValues(), 'pdldata.opprettNyPerson.identtype')
	}
}

const getAdressebeskyttelseOptions = (identtype: string) => {
	if (identtype === 'NPID') {
		return [{ value: 'STRENGT_FORTROLIG_UTLAND', label: 'Strengt fortrolig utland' }]
	}
	return identtype === 'FNR'
		? Options('gradering')
		: Options('gradering').filter(
				(v: Target) => !['STRENGT_FORTROLIG', 'FORTROLIG'].includes(v.value),
			)
}

export const AdressebeskyttelseForm = ({
	formMethods,
	path,
	idx,
	identtype,
}: AdressebeskyttelseFormValues) => {
	const [options, setOptions] = useState(getAdressebeskyttelseOptions(identtype))

	useEffect(() => {
		const newOptions = getAdressebeskyttelseOptions(identtype)
		const selectedOption = _.get(formMethods.getValues(), `${path}.gradering`)
		if (selectedOption && !newOptions.map((opt) => opt.value).includes(selectedOption)) {
			formMethods.setValue(`${path}.gradering`, null)
			formMethods.trigger()
		}
		setOptions(newOptions)
	}, [identtype])

	const handleChangeGradering = (target: Target) => {
		const adressebeskyttelse = _.get(formMethods.getValues(), path)
		const adressebeskyttelseClone = _.cloneDeep(adressebeskyttelse)
		_.set(adressebeskyttelseClone, 'gradering', target?.value || null)
		if (target?.value === 'STRENGT_FORTROLIG_UTLAND') {
			_.set(adressebeskyttelseClone, 'master', 'PDL')
		} else {
			identtype !== 'NPID' && _.set(adressebeskyttelseClone, 'master', 'FREG')
		}
		formMethods.setValue(path, adressebeskyttelseClone)
		formMethods.trigger()
	}
	return (
		<React.Fragment key={idx}>
			<div className="flexbox--full-width">
				<FormikSelect
					name={`${path}.gradering`}
					label="Gradering"
					options={options}
					onChange={(target: Target) => handleChangeGradering(target)}
					size="large"
				/>
			</div>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</React.Fragment>
	)
}

export const Adressebeskyttelse = ({ formMethods }: AdressebeskyttelseValues) => {
	const opts = useContext(BestillingsveilederContext)
	const identtype = getIdenttype(formMethods, opts.identtype)
	return (
		<Kategori title="Adressebeskyttelse">
			<FormikDollyFieldArray
				name="pdldata.person.adressebeskyttelse"
				header="Adressebeskyttelse"
				newEntry={getInitialAdressebeskyttelse(identtype === 'NPID' ? 'PDL' : 'FREG')}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<AdressebeskyttelseForm
						formMethods={formMethods}
						path={path}
						idx={idx}
						identtype={identtype}
					/>
				)}
			</FormikDollyFieldArray>
		</Kategori>
	)
}

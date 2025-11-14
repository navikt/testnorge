import React, { useEffect, useState } from 'react'
import { getInitialAdressebeskyttelse } from '@/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import * as _ from 'lodash-es'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface AdressebeskyttelseValues {
	formMethods: UseFormReturn
}

type AdressebeskyttelseFormValues = {
	formMethods: UseFormReturn
	path: string
	idx?: number
	identtype?: string
	identMaster?: string
}

type Target = {
	label: string
	value: string
}

export const getIdenttype = (formMethods: any, identtype: string) => {
	const nyIdenttype = formMethods.watch('pdldata.person.nyident[0].identtype')
	if (nyIdenttype) {
		return nyIdenttype
	} else {
		return identtype ? identtype : formMethods.watch('pdldata.opprettNyPerson.identtype')
	}
}

const getAdressebeskyttelseOptions = (identtype: string, identMaster: string) => {
	if (identtype === 'NPID' || identMaster === 'PDL') {
		return [{ value: 'STRENGT_FORTROLIG_UTLAND', label: 'Strengt fortrolig utland' }]
	}
	return identtype === 'FNR'
		? Options('gradering')
		: Options('gradering').filter((v: Target) => v.value !== 'FORTROLIG')
}

export const AdressebeskyttelseForm = ({
	formMethods,
	path,
	idx,
	identtype,
	identMaster,
}: AdressebeskyttelseFormValues) => {
	const [options, setOptions] = useState(getAdressebeskyttelseOptions(identtype, identMaster))

	useEffect(() => {
		const newOptions = getAdressebeskyttelseOptions(identtype, identMaster)
		const selectedOption = formMethods.watch(`${path}.gradering`)
		if (selectedOption && !newOptions.map((opt) => opt.value).includes(selectedOption)) {
			formMethods.setValue(`${path}.gradering`, null)
			formMethods.trigger(path)
		}
		setOptions(newOptions)
	}, [identtype])

	const handleChangeGradering = (target: Target) => {
		const adressebeskyttelse = formMethods.watch(path)
		const adressebeskyttelseClone = _.cloneDeep(adressebeskyttelse)
		_.set(adressebeskyttelseClone, 'gradering', target?.value || null)
		if (target?.value === 'STRENGT_FORTROLIG_UTLAND') {
			_.set(adressebeskyttelseClone, 'master', 'PDL')
		} else {
			identtype !== 'NPID' && _.set(adressebeskyttelseClone, 'master', 'FREG')
		}
		formMethods.setValue(path, adressebeskyttelseClone)
		formMethods.trigger(path)
	}
	return (
		<React.Fragment key={idx}>
			<div className="flexbox--full-width">
				<FormSelect
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
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const initialPdlMaster = opts.identMaster !== 'PDLF' && opts?.identtype === 'NPID'

	return (
		<Kategori title="Adressebeskyttelse">
			<FormDollyFieldArray
				name="pdldata.person.adressebeskyttelse"
				header="Adressebeskyttelse"
				newEntry={getInitialAdressebeskyttelse(initialPdlMaster ? 'PDL' : 'FREG')}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<AdressebeskyttelseForm
						formMethods={formMethods}
						path={path}
						idx={idx}
						identtype={opts?.identtype}
						identMaster={opts?.identMaster}
					/>
				)}
			</FormDollyFieldArray>
		</Kategori>
	)
}

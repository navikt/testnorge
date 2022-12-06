import React, { useContext, useEffect, useState } from 'react'
import { initialAdressebeskyttelse } from '@/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import _get from 'lodash/get'
import _cloneDeep from 'lodash/cloneDeep'
import _set from 'lodash/set'
import { FormikProps } from 'formik'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/Bestillingsveileder'

interface AdressebeskyttelseValues {
	formikBag: FormikProps<{}>
}

type AdressebeskyttelseFormValues = {
	formikBag: FormikProps<{}>
	path: string
	idx?: number
	identtype?: string
}

type Target = {
	label: string
	value: string
}

export const getIdenttype = (formikBag: any, identtype: string) => {
	const nyIdenttype = _get(formikBag.values, 'pdldata.person.nyident[0].identtype')
	if (nyIdenttype) {
		return nyIdenttype
	} else {
		return identtype ? identtype : _get(formikBag.values, 'pdldata.opprettNyPerson.identtype')
	}
}

const getAdressebeskyttelseOptions = (identtype: string) => {
	return identtype === 'FNR'
		? Options('gradering')
		: Options('gradering').filter(
				(v: Target) => !['STRENGT_FORTROLIG', 'FORTROLIG'].includes(v.value)
		  )
}

export const AdressebeskyttelseForm = ({
	formikBag,
	path,
	idx,
	identtype,
}: AdressebeskyttelseFormValues) => {
	const [options, setOptions] = useState(getAdressebeskyttelseOptions(identtype))

	useEffect(() => {
		const newOptions = getAdressebeskyttelseOptions(identtype)
		const selectedOption = _get(formikBag.values, `${path}.gradering`)
		if (selectedOption && !newOptions.map((opt) => opt.value).includes(selectedOption)) {
			formikBag.setFieldValue(`${path}.gradering`, null)
		}
		setOptions(newOptions)
	}, [identtype])

	const handleChangeGradering = (target: Target) => {
		const adressebeskyttelse = _get(formikBag.values, path)
		const adressebeskyttelseClone = _cloneDeep(adressebeskyttelse)
		_set(adressebeskyttelseClone, 'gradering', target?.value || null)
		if (target?.value === 'STRENGT_FORTROLIG_UTLAND') {
			_set(adressebeskyttelseClone, 'master', 'PDL')
		} else {
			_set(adressebeskyttelseClone, 'master', 'FREG')
		}
		formikBag.setFieldValue(path, adressebeskyttelseClone)
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

export const Adressebeskyttelse = ({ formikBag }: AdressebeskyttelseValues) => {
	const opts = useContext(BestillingsveilederContext)
	const identtype = getIdenttype(formikBag, opts.identtype)
	return (
		<Kategori title="Adressebeskyttelse">
			<FormikDollyFieldArray
				name="pdldata.person.adressebeskyttelse"
				header="Adressebeskyttelse"
				newEntry={initialAdressebeskyttelse}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<AdressebeskyttelseForm
						formikBag={formikBag}
						path={path}
						idx={idx}
						identtype={identtype}
					/>
				)}
			</FormikDollyFieldArray>
		</Kategori>
	)
}

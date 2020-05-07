import React from 'react'
import _get from 'lodash/get'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'

const initialValues = {
	egenskap: '',
	fratraadt: false,
	registringsDato: ''
}

export const PersonrollerForm = ({ formikBag, path }) => {
	const getEgenskapOptions = () => {
		const valgteOptions = []
		const personroller = _get(formikBag.values, `${path}.personroller`)
		if (personroller) {
			personroller.forEach(rolle => {
				valgteOptions.push(rolle.egenskap)
			})
		}
		return Options('rolleEgenskap').filter(option => !valgteOptions.includes(option.value))
	}
	const egenskapOptions = getEgenskapOptions()

	return (
		<FormikDollyFieldArray
			name={`${path}.personroller`}
			header="Personrolle"
			newEntry={initialValues}
			isFull={_get(formikBag.values, `${path}.personroller`).length > 4}
			title={
				_get(formikBag.values, `${path}.personroller`).length > 4
					? 'Alle mulige personroller er lagt til'
					: null
			}
		>
			{path => (
				<>
					<DollySelect
						name={`${path}.egenskap`}
						label="Egenskap"
						options={egenskapOptions}
						onChange={egenskap => formikBag.setFieldValue(`${path}.egenskap`, egenskap.value)}
						value={_get(formikBag.values, `${path}.egenskap`)}
						placeholder={
							_get(formikBag.values, `${path}.egenskap`)
								? _get(formikBag.values, `${path}.egenskap`)
								: 'Velg..'
						}
						isClearable={false}
						feil={
							_get(formikBag.values, `${path}.egenskap`) === '' && {
								feilmelding: 'Feltet er påkrevd'
							}
						}
					/>
					<FormikDatepicker name={`${path}.registringsDato`} label="Registreringsdato" />
					<FormikCheckbox name={`${path}.fratraadt`} label="Har fratrådt" checkboxMargin />
				</>
			)}
		</FormikDollyFieldArray>
	)
}

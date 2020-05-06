import React from 'react'
import _get from 'lodash/get'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

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

	// TODO: Få Egenskap-select til å vise value (selv om den ikke er i options)
	return (
		<FormikDollyFieldArray
			name={`${path}.personroller`}
			header="Personrolle"
			newEntry={initialValues}
		>
			{path => (
				<>
					<FormikSelect
						name={`${path}.egenskap`}
						label="Egenskap"
						options={egenskapOptions}
						size="medium"
					/>
					<FormikDatepicker name={`${path}.registringsDato`} label="Registreringsdato" />
					<FormikCheckbox name={`${path}.fratraadt`} label="Har fratrådt" checkboxMargin />
				</>
			)}
		</FormikDollyFieldArray>
	)
}

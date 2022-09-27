import React from 'react'
import _get from 'lodash/get'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'

const initialValues = {
	egenskap: '',
	fratraadt: false,
}

export const PersonrollerForm = ({ formikBag, path }) => {
	const personroller = _get(formikBag.values, `${path}.personroller`)

	const getEgenskapOptions = () => {
		const valgteOptions = []
		if (personroller) {
			personroller.forEach((rolle) => {
				valgteOptions.push(rolle.egenskap)
			})
		}
		return Options('rolleEgenskap').filter((option) => !valgteOptions.includes(option.value))
	}

	const egenskapOptions = getEgenskapOptions()
	const colorStyles = {
		placeholder: (defaultStyles) => {
			return {
				...defaultStyles,
				color: '#000000',
			}
		},
	}

	return (
		<FormikDollyFieldArray
			name={`${path}.personroller`}
			header="Personrolle"
			newEntry={initialValues}
			maxEntries={5} // Det finnes fem ulike egenskaper for personroller, som hver kan velges én gang
			maxReachedDescription={'Alle mulige personroller er lagt til'}
		>
			{(path) => {
				const egenskap = `${path}.egenskap`
				return (
					<>
						<DollySelect
							name={egenskap}
							label="Egenskap"
							options={egenskapOptions}
							onChange={(egenskapen) => formikBag.setFieldValue(egenskap, egenskapen.value)}
							value={_get(formikBag.values, egenskap)}
							placeholder={
								_get(formikBag.values, egenskap) ? _get(formikBag.values, egenskap) : 'Velg..'
							}
							isClearable={false}
							feil={
								_get(formikBag.values, egenskap) === '' && {
									feilmelding: 'Feltet er påkrevd',
								}
							}
							styles={_get(formikBag.values, egenskap) ? colorStyles : null}
						/>
						<FormikCheckbox name={`${path}.fratraadt`} label="Har fratrådt" checkboxMargin />
					</>
				)
			}}
		</FormikDollyFieldArray>
	)
}

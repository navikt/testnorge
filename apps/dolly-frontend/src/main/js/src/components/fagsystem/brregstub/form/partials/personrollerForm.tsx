import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import React from 'react'

const initialValues = {
	egenskap: '',
	fratraadt: false,
}

export const PersonrollerForm = ({ formMethods, path }) => {
	const personroller = formMethods.watch(`${path}.personroller`)

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
		<FormDollyFieldArray
			name={`${path}.personroller`}
			header="Personrolle"
			newEntry={initialValues}
			maxEntries={5} // Det finnes fem ulike egenskaper for personroller, som hver kan velges én gang
			maxReachedDescription={'Alle mulige personroller er lagt til'}
		>
			{(path) => {
				const egenskap = `${path}.egenskap`
				return (
					<React.Fragment key={path}>
						<DollySelect
							name={egenskap}
							label="Egenskap"
							options={egenskapOptions}
							onChange={(egenskapen) => formMethods.setValue(egenskap, egenskapen.value)}
							value={formMethods.watch(egenskap)}
							placeholder={formMethods.watch(egenskap) ? formMethods.watch(egenskap) : 'Velg ...'}
							isClearable={false}
							styles={formMethods.watch(egenskap) ? colorStyles : null}
						/>
						<FormCheckbox
							name={`${path}.fratraadt`}
							label="Har fratrådt"
							id={`${path}.fratraadt`}
							checkboxMargin
						/>
					</React.Fragment>
				)
			}}
		</FormDollyFieldArray>
	)
}

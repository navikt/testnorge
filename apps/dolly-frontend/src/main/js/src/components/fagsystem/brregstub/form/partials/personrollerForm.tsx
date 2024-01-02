import * as _ from 'lodash'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'

const initialValues = {
	egenskap: '',
	fratraadt: false,
}

export const PersonrollerForm = ({ formMethods, path }) => {
	const personroller = _.get(formMethods.getValues(), `${path}.personroller`)

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
							onChange={(egenskapen) => formMethods.setValue(egenskap, egenskapen.value)}
							value={_.get(formMethods.getValues(), egenskap)}
							placeholder={
								_.get(formMethods.getValues(), egenskap)
									? _.get(formMethods.getValues(), egenskap)
									: 'Velg...'
							}
							isClearable={false}
							styles={_.get(formMethods.getValues(), egenskap) ? colorStyles : null}
						/>
						<FormikCheckbox name={`${path}.fratraadt`} label="Har fratrådt" checkboxMargin />
					</>
				)
			}}
		</FormikDollyFieldArray>
	)
}

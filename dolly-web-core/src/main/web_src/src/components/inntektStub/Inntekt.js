import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import texts from './texts'
import { Field } from 'formik'

const sjekkFelt = (field, options) => {
	console.log('field :', field)
	console.log('options :', options)
	if (!options.includes('<TOM>')) {
		return { feilmelding: 'Feltet er påkrevd' }
	}
}

const fieldReslover = (field, options = [], handleChange) => {
	if (options.length == 1 && options[0] === '<TOM>') {
		return
	} else if (
		(options.length == 2 && options.includes('<TOM>') && options.includes('<UTFYLT>')) ||
		(options.length == 1 && options[0] === '<UTFYLT>')
	) {
		return (
			<FormikTextInput
				visHvisAvhuket={false}
				name={field}
				label={texts(field)}
				onBlur={handleChange}
				size="large"
				feil={sjekkFelt(field, options)}
				// value={}
			/>
		)
		// type="number"
	} else if (options.length > 0 && typeof options[0] === 'boolean') {
		return (
			<FormikCheckbox
				name={field}
				label={texts(field)}
				afterChange={handleChange}
				disabled={options.length == 1 && options[0] === false}
			/>
		)
	}

	return (
		<FormikSelect
			name={field}
			label={texts(field)}
			options={options.map(option => ({ label: texts(option), value: option }))}
			fastfield={false}
			afterChange={handleChange}
			size="large"
			feil={sjekkFelt(field, options)}
			// required={!options.includes('<TOM>')}
		/>
	)
}

const Inntekt = ({ fields = {}, onValidate }) => (
	<div>
		{fieldReslover(
			'inntektstype',
			['LOENNSINNTEKT', 'YTELSE_FRA_OFFENTLIGE', 'PENSJON_ELLER_TRYGD', 'NAERINGSINNTEKT'],
			onValidate
		)}
		<FormikTextInput
			visHvisAvhuket={false}
			name="beloep"
			label="Beløp"
			onBlur={onValidate}
			size="large"
			type="number"
			// feil={sjekkFelt(field, options)}
		/>
		{Object.keys(fields)
			.filter(field => !(fields[field].length === 1 && fields[field][0] === '<TOM>'))
			.map(field => fieldReslover(field, fields[field], onValidate))}
	</div>
)

Inntekt.displayName = 'Inntekt'

export default Inntekt

import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import texts from '../texts'

const sjekkFelt = options => {
	if (!options.includes('<TOM>')) {
		return { feilmelding: 'Feltet er påkrevd' }
	}
}

const dateFields = [
	'etterbetalingsperiodeStart',
	'etterbetalingsperiodeSlutt',
	'pensjonTidsromStart',
	'pensjonTidsromSlutt'
]

const numberFields = [
	'grunnpensjonsbeloep',
	'heravEtterlattepensjon',
	'pensjonsgrad',
	'tilleggspensjonsbeloep',
	'ufoeregrad',
	'antall'
]

const fieldReslover = (field, options = [], handleChange) => {
	if (dateFields.includes(field)) {
		return (
			<FormikDatepicker
				visHvisAvhuket={false}
				name={field}
				label={texts(field)}
				onBlur={handleChange}
				feil={sjekkFelt(options)}
			/>
		)
	} else if (field === 'skattemessigBosattILand' || field === 'opptjeningsland') {
		return (
			<FormikSelect
				name={field}
				label={texts(field)}
				kodeverk="LandkoderISO2"
				fastfield={false}
				afterChange={handleChange}
				size="large"
				feil={sjekkFelt(options)}
			/>
		)
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
				feil={sjekkFelt(options)}
				type={numberFields.includes(field) ? 'number' : 'text'}
			/>
		)
	}
	// else if (options.length > 0 && typeof options[0] === 'boolean') {
	// 	return (
	// 		<FormikCheckbox
	// 			name={field}
	// 			label={texts(field)}
	// 			afterChange={handleChange}
	// 			disabled={options.length == 1 && options[0] === false}
	// 		/>
	// 	)
	// }

	return (
		<FormikSelect
			name={field}
			label={texts(field)}
			options={options
				.filter(option => option !== '<TOM>')
				.map(option => ({ label: texts(option), value: option }))}
			fastfield={false}
			afterChange={handleChange}
			size="large"
			feil={sjekkFelt(options)}
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

		{Object.keys(fields)
			.filter(field => !(fields[field].length === 1 && fields[field][0] === '<TOM>'))
			.map(field => fieldReslover(field, fields[field], onValidate))}
	</div>
)

Inntekt.displayName = 'Inntekt'

export default Inntekt

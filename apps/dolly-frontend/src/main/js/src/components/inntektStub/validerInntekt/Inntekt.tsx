import _ from 'lodash'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import texts from '@/components/inntektStub/texts'
import tilleggsinformasjonPaths from '@/components/inntektStub/paths'

const sjekkFelt = (formMethods, field, options, values, path) => {
	const { watch, getFieldState, setError } = formMethods
	const fieldValue = watch(path)
	const existingError = getFieldState(`${path}.${field}`)?.error
	const fieldPath = tilleggsinformasjonPaths(field)
	const val = _.get(fieldValue, fieldPath)

	if (
		!options.includes('<TOM>') &&
		!existingError &&
		((fieldValue && !val && val !== false) || (!optionsUtfylt(options) && !options.includes(val)))
	) {
		setError(`${path}.${field}`, { message: 'Feltet er påkrevd' })
	}
	return null
}

const dateFields = [
	'etterbetalingsperiodeStart',
	'etterbetalingsperiodeSlutt',
	'pensjonTidsromStart',
	'pensjonTidsromSlutt',
]

const numberFields = [
	'grunnpensjonsbeloep',
	'heravEtterlattepensjon',
	'pensjonsgrad',
	'tilleggspensjonsbeloep',
	'ufoeregrad',
	'antall',
]

const wideFields = ['beskrivelse', 'inntjeningsforhold', 'persontype']

const booleanField = (options) => {
	return options.length > 0 && typeof options[0] === 'boolean'
}

function optionsUtfylt(options) {
	return (
		(options.length === 2 && options.includes('<TOM>') && options.includes('<UTFYLT>')) ||
		(options.length === 1 && options[0] === '<UTFYLT>')
	)
}

const fieldResolver = (field, handleChange, formMethods, path, index, options = []) => {
	const fieldName = tilleggsinformasjonPaths(field)
	const values = formMethods.getValues()

	if (dateFields.includes(field)) {
		return (
			<FormikDatepicker
				key={index}
				visHvisAvhuket={false}
				name={`${path}.${fieldName}`}
				label={texts(field)}
				afterChange={handleChange}
				feil={sjekkFelt(formMethods, field, options, values, path)}
			/>
		)
	} else if (field === 'skattemessigBosattILand' || field === 'opptjeningsland') {
		return (
			<FormikSelect
				key={index}
				name={`${path}.${fieldName}`}
				label={texts(field)}
				kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
				afterChange={handleChange}
				size="large"
				feil={sjekkFelt(formMethods, field, options, values, path)}
			/>
		)
	} else if (optionsUtfylt(options)) {
		return (
			<FormikTextInput
				key={index}
				visHvisAvhuket={false}
				name={`${path}.${fieldName}`}
				label={texts(field)}
				onSubmit={handleChange}
				size={numberFields.includes(field) ? 'medium' : 'large'}
				feil={sjekkFelt(formMethods, field, options, values, path)}
				type={numberFields.includes(field) ? 'number' : 'text'}
			/>
		)
	}
	const filteredOptions = options.map((option) => ({ label: texts(option), value: option }))
	const fieldPath = `${path}.${tilleggsinformasjonPaths(field)}`

	return (
		<FormikSelect
			key={index}
			name={`${path}.${fieldName}`}
			value={_.get(values, fieldPath)}
			label={texts(field)}
			options={filteredOptions.filter((option) => option.value !== '<TOM>')}
			afterChange={handleChange}
			size={booleanField(options) ? 'small' : wideFields.includes(field) ? 'xxlarge' : 'large'}
			feil={sjekkFelt(formMethods, field, options, values, path)}
			isClearable={field !== 'inntektstype'}
		/>
	)
}

const Inntekt = ({ fields = {}, onValidate, formMethods, path }) => {
	return (
		<div className="flexbox--flex-wrap">
			{fieldResolver('inntektstype', onValidate, formMethods, path, `${path}.inntektstype`, [
				'LOENNSINNTEKT',
				'YTELSE_FRA_OFFENTLIGE',
				'PENSJON_ELLER_TRYGD',
				'NAERINGSINNTEKT',
			])}
			{Object.keys(fields)
				.filter((field) => !(fields[field].length === 1 && fields[field][0] === '<TOM>'))
				.map((field) =>
					fieldResolver(field, onValidate, formMethods, path, `${path}.${field}`, fields[field]),
				)}
		</div>
	)
}

Inntekt.displayName = 'Inntekt'

export default Inntekt

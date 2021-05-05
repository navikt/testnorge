import React, { useEffect } from 'react'
import _get from 'lodash/get'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import texts from '../texts'
import tilleggsinformasjonPaths from '../paths'

const sjekkFelt = (field, options, values, path) => {
	const fieldValue = _get(values, path)
	const fieldPath = tilleggsinformasjonPaths(field)
	if (!options.includes('<TOM>')) {
		if (fieldValue && !_get(fieldValue, fieldPath) && _get(fieldValue, fieldPath) !== false) {
			return { feilmelding: 'Feltet er påkrevd' }
		}
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

const wideFields = ['beskrivelse', 'inntjeningsforhold', 'persontype']

const booleanField = options => {
	return options.length > 0 && typeof options[0] === 'boolean'
}

function optionsUtfylt(options) {
	return (
		(options.length === 2 && options.includes('<TOM>') && options.includes('<UTFYLT>')) ||
		(options.length === 1 && options[0] === '<UTFYLT>')
	)
}

const fieldResolver = (field, handleChange, formik, path, index, resetForm, options = []) => {
	const values = formik.values
	if (dateFields.includes(field)) {
		return (
			<FormikDatepicker
				key={index}
				visHvisAvhuket={false}
				name={field}
				label={texts(field)}
				afterChange={handleChange}
				feil={sjekkFelt(field, options, values, path)}
			/>
		)
	} else if (field === 'skattemessigBosattILand' || field === 'opptjeningsland') {
		return (
			<FormikSelect
				key={index}
				name={field}
				label={texts(field)}
				kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
				fastfield={false}
				afterChange={handleChange}
				size="large"
				feil={sjekkFelt(field, options, values, path)}
			/>
		)
	} else if (optionsUtfylt(options)) {
		return (
			<FormikTextInput
				key={index}
				visHvisAvhuket={false}
				name={field}
				label={texts(field)}
				onSubmit={handleChange}
				size={numberFields.includes(field) ? 'medium' : 'large'}
				feil={sjekkFelt(field, options, values, path)}
				type={numberFields.includes(field) ? 'number' : 'text'}
			/>
		)
	}
	const filteredOptions = options.map(option => ({ label: texts(option), value: option }))
	const fieldPath = `${path}.${tilleggsinformasjonPaths(field)}`
	if (
		!resetForm &&
		filteredOptions.length === 1 &&
		_get(values, fieldPath) !== filteredOptions[0].value
	) {
		useEffect(() => {
			formik.setFieldValue(fieldPath, filteredOptions[0].value)
		})
	}
	return (
		<FormikSelect
			key={index}
			name={field}
			value={filteredOptions.length === 1 ? filteredOptions[0].value : _get(values, fieldPath)}
			label={texts(field)}
			options={filteredOptions.filter(option => option.value !== '<TOM>')}
			fastfield={false}
			afterChange={handleChange}
			size={booleanField(options) ? 'small' : wideFields.includes(field) ? 'xxlarge' : 'large'}
			feil={sjekkFelt(field, filteredOptions, values, path)}
			isClearable={field !== 'inntektstype' && filteredOptions.length !== 1}
		/>
	)
}

const Inntekt = ({ fields = {}, onValidate, formikBag, path, resetForm }) => (
	<div className="flexbox--flex-wrap">
		{fieldResolver('inntektstype', onValidate, formikBag, path, `${path}.inntektstype`, resetForm, [
			'LOENNSINNTEKT',
			'YTELSE_FRA_OFFENTLIGE',
			'PENSJON_ELLER_TRYGD',
			'NAERINGSINNTEKT'
		])}

		{Object.keys(fields)
			.filter(field => !(fields[field].length === 1 && fields[field][0] === '<TOM>'))
			.map(field =>
				fieldResolver(
					field,
					onValidate,
					formikBag,
					path,
					`${path}.${field}`,
					resetForm,
					fields[field]
				)
			)}
	</div>
)

Inntekt.displayName = 'Inntekt'

export default Inntekt

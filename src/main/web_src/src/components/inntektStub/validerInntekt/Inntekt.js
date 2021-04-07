import React from 'react'
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

const fieldResolver = (field, options = [], handleChange, values, path, index) => {
	if (dateFields.includes(field)) {
		return (
			<FormikDatepicker
				key={index}
				visHvisAvhuket={false}
				name={field}
				label={texts(field)}
				onBlur={handleChange}
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
	} else if (
		(options.length === 2 && options.includes('<TOM>') && options.includes('<UTFYLT>')) ||
		(options.length === 1 && options[0] === '<UTFYLT>')
	) {
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
	return (
		<FormikSelect
			key={index}
			name={field}
			label={texts(field)}
			options={options
				.filter(option => option !== '<TOM>')
				.map(option => ({ label: texts(option), value: option }))}
			fastfield={false}
			afterChange={handleChange}
			size={booleanField(options) ? 'small' : wideFields.includes(field) ? 'xxlarge' : 'large'}
			feil={sjekkFelt(field, options, values, path)}
			isClearable={field !== 'inntektstype'}
		/>
	)
}

const Inntekt = ({ fields = {}, onValidate, formikBag, path }) => (
	<div className="flexbox--flex-wrap">
		{fieldResolver(
			'inntektstype',
			['LOENNSINNTEKT', 'YTELSE_FRA_OFFENTLIGE', 'PENSJON_ELLER_TRYGD', 'NAERINGSINNTEKT'],
			onValidate,
			formikBag.values,
			path
		)}

		{Object.keys(fields)
			.filter(field => !(fields[field].length === 1 && fields[field][0] === '<TOM>'))
			.map(field =>
				fieldResolver(field, fields[field], onValidate, formikBag.values, path, `${path}.${field}`)
			)}
	</div>
)

Inntekt.displayName = 'Inntekt'

export default Inntekt

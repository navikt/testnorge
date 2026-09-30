import { useEffect } from 'react'
import * as _ from 'lodash-es'
import { useFormState, useWatch, UseFormReturn } from 'react-hook-form'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import texts from '@/components/fagsystem/inntektstub/validerInntekt/texts'
import tilleggsinformasjonPaths from '@/components/fagsystem/inntektstub/validerInntekt/paths'

type FeltOptions = Array<string | boolean>

const inntektstypeOptions: FeltOptions = [
	'LOENNSINNTEKT',
	'YTELSE_FRA_OFFENTLIGE',
	'PENSJON_ELLER_TRYGD',
	'NAERINGSINNTEKT',
]

const ingenOptions: FeltOptions = []

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

const booleanField = (options: FeltOptions) => {
	return options.length > 0 && typeof options[0] === 'boolean'
}

function optionsUtfylt(options: FeltOptions) {
	return (
		(options.length === 2 && options.includes('<TOM>') && options.includes('<UTFYLT>')) ||
		(options.length === 1 && options[0] === '<UTFYLT>')
	)
}

const erFeltPaakrevd = (options: FeltOptions, inntektValue: any, feltValue: any) => {
	if (options.includes('<TOM>')) {
		return false
	}
	return (
		(inntektValue && !feltValue && feltValue !== false) ||
		(!optionsUtfylt(options) && !options.includes(feltValue))
	)
}

const hentEnesteValg = (options: FeltOptions) => {
	if (options.length === 1 && options[0] !== '<TOM>' && options[0] !== '<UTFYLT>') {
		return options[0]
	}
	return undefined
}

interface InntektFeltProps {
	field: string
	handleChange: () => void
	formMethods: UseFormReturn
	path: string
	options?: FeltOptions
}

const InntektFelt = ({
	field,
	handleChange,
	formMethods,
	path,
	options = ingenOptions,
}: InntektFeltProps) => {
	const fieldName = tilleggsinformasjonPaths(field)
	const fieldPath = `${path}.${fieldName}`

	const inntektValue = useWatch({ control: formMethods.control, name: path })
	const value = _.get(inntektValue, fieldName)

	const { errors } = useFormState({ control: formMethods.control, name: fieldPath })
	const harFeil = !!_.get(errors, fieldPath)

	const enesteValg = hentEnesteValg(options)
	const paakrevd = enesteValg === undefined && erFeltPaakrevd(options, inntektValue, value)

	useEffect(() => {
		if (enesteValg !== undefined && value !== enesteValg) {
			formMethods.setValue(fieldPath, enesteValg, { shouldDirty: true, shouldValidate: true })
		}
	}, [enesteValg, fieldPath, value])

	useEffect(() => {
		if (paakrevd && !harFeil) {
			formMethods.setError(fieldPath, { message: 'Feltet er påkrevd' })
		}
	}, [paakrevd, harFeil, fieldPath])

	if (dateFields.includes(field)) {
		return <FormDatepicker visHvisAvhuket={false} name={fieldPath} label={texts(field)} />
	}

	if (field === 'skattemessigBosattILand' || field === 'opptjeningsland') {
		return (
			<FormSelect
				name={fieldPath}
				label={texts(field)}
				kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
				afterChange={handleChange}
				size="large"
			/>
		)
	}

	if (optionsUtfylt(options)) {
		return (
			<FormTextInput
				visHvisAvhuket={false}
				name={fieldPath}
				label={texts(field)}
				onSubmit={handleChange}
				size={numberFields.includes(field) ? 'medium' : 'large'}
				type={numberFields.includes(field) ? 'number' : 'text'}
			/>
		)
	}

	const labelValueOptions = options.map((option) => ({ label: texts(option), value: option }))

	return (
		<FormSelect
			name={fieldPath}
			value={value}
			label={texts(field)}
			options={labelValueOptions.filter((option) => option.value !== '<TOM>')}
			afterChange={handleChange}
			size={booleanField(options) ? 'small' : wideFields.includes(field) ? 'xxlarge' : 'large'}
			isClearable={field !== 'inntektstype' && field !== 'beskrivelse'}
		/>
	)
}

const Inntekt = ({ fields = {}, onValidate, formMethods, path }) => {
	console.log('fields: ', fields) //TODO - SLETT MEG
	return (
		<div className="flexbox--flex-wrap">
			<InntektFelt
				key={`${path}.inntektstype`}
				field="inntektstype"
				handleChange={onValidate}
				formMethods={formMethods}
				path={path}
				options={inntektstypeOptions}
			/>
			{Object.keys(fields)
				.filter((field) => !(fields[field].length === 1 && fields[field][0] === '<TOM>'))
				.map((field) => (
					<InntektFelt
						key={`${path}.${field}`}
						field={field}
						handleChange={onValidate}
						formMethods={formMethods}
						path={path}
						options={fields[field]}
					/>
				))}
		</div>
	)
}

Inntekt.displayName = 'Inntekt'

export default Inntekt

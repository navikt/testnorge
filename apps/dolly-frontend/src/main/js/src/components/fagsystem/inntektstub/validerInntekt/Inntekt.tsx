import { useEffect, useRef } from 'react'
import * as _ from 'lodash-es'
import { useWatch, UseFormReturn } from 'react-hook-form'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import texts from '@/components/fagsystem/inntektstub/validerInntekt/texts'
import tilleggsinformasjonPaths from '@/components/fagsystem/inntektstub/validerInntekt/paths'
import { initialValues } from '@/components/fagsystem/inntektstub/form/partials/inntektsinformasjonLister/inntektForm'
import {
	FeltOptions,
	hentEnesteValg,
	optionsUtfylt,
} from '@/components/fagsystem/inntektstub/validerInntekt/gyldigeVerdier'

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

interface InntektFeltProps {
	field: string
	handleChange: () => void
	formMethods: UseFormReturn
	path: string
	options?: FeltOptions
	size?: string | null
	isLoading?: boolean
}

const InntektFelt = ({
	field,
	handleChange,
	formMethods,
	path,
	options = ingenOptions,
	size = null,
	isLoading = false,
}: InntektFeltProps) => {
	const fieldName = tilleggsinformasjonPaths(field)
	const fieldPath = `${path}.${fieldName}`

	const inntektValue = useWatch({ control: formMethods.control, name: path })
	const value = _.get(inntektValue, fieldName)

	const enesteValg = hentEnesteValg(options)

	const sisteAutoutfylteValgRef = useRef<string | boolean | undefined>(undefined)

	useEffect(() => {
		if (enesteValg === undefined) {
			sisteAutoutfylteValgRef.current = undefined
			return
		}
		if (sisteAutoutfylteValgRef.current === enesteValg) {
			return
		}
		sisteAutoutfylteValgRef.current = enesteValg
		if (value !== enesteValg) {
			formMethods.setValue(fieldPath, enesteValg, { shouldDirty: true, shouldValidate: true })
		}
	}, [enesteValg, fieldPath, value, formMethods])

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
				isClearable={true}
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
				size={size ?? (numberFields.includes(field) ? 'medium' : 'large')}
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
			size={
				size ?? (booleanField(options) ? 'small' : wideFields.includes(field) ? 'xxlarge' : 'large')
			}
			isClearable={true}
			isLoading={isLoading}
		/>
	)
}

const Inntekt = ({
	fields = {} as any,
	isLoadingFields = false,
	onValidate,
	formMethods,
	path,
}) => {
	return (
		<>
			<InntektFelt
				key={`${path}.inntektstype`}
				field="inntektstype"
				handleChange={(val) => {
					formMethods.setValue(path, {
						...initialValues,
						beloep: formMethods.getValues(`${path}.beloep`),
						inntektstype: val ? val.value : '',
						beskrivelse: '',
					})
					onValidate()
				}}
				formMethods={formMethods}
				path={path}
				options={inntektstypeOptions}
				size="medium"
			/>
			{formMethods.watch(`${path}.inntektstype`) && (
				<InntektFelt
					key={`${path}.beskrivelse`}
					field="beskrivelse"
					handleChange={onValidate}
					formMethods={formMethods}
					path={path}
					options={fields?.beskrivelse ?? []}
					size="xlarge"
					isLoading={isLoadingFields}
				/>
			)}
			{formMethods.watch(`${path}.beskrivelse`) && (
				<div style={{ display: 'contents' }}>
					{Object.keys(fields)
						.filter((field) => field !== 'beskrivelse')
						.filter((field) => !(fields[field].length === 1 && fields[field][0] === '<TOM>'))
						.map((field) => (
							<InntektFelt
								key={`${path}.${field}`}
								field={field}
								handleChange={onValidate}
								formMethods={formMethods}
								path={path}
								options={fields[field]}
								isLoading={isLoadingFields}
							/>
						))}
				</div>
			)}
		</>
	)
}

Inntekt.displayName = 'Inntekt'

export default Inntekt

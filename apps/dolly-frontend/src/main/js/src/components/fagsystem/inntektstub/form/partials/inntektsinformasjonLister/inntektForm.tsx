import React, { useEffect } from 'react'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import InntektStub from '@/components/inntektStub/validerInntekt'
import { useBoolean } from 'react-use'
import { ToggleGroup } from '@navikt/ds-react'
import { UseFormReturn } from 'react-hook-form/dist/types'

const INNTEKTSTYPE_FORENKLET_TOGGLE = 'INNTEKTSTYPE_FORENKLET_TOGGLE'

export enum FormType {
	STANDARD = 'standard',
	FORENKLET = 'forenklet',
}

const initialValues = {
	beloep: '',
	startOpptjeningsperiode: '',
	sluttOpptjeningsperiode: '',
	inntektstype: '',
}

const simpleInitialValues = {
	beloep: '',
	startOpptjeningsperiode: '',
	sluttOpptjeningsperiode: '',
	inntektstype: 'LOENNSINNTEKT',
	inngaarIGrunnlagForTrekk: true,
	utloeserArbeidsgiveravgift: true,
	fordel: 'kontantytelse',
	beskrivelse: 'fastloenn',
}

type inntekt = {
	beloep: string
	startOpptjeningsperiode: string
	sluttOpptjeningsperiode: string
	inntektstype: string
	inngaarIGrunnlagForTrekk: boolean
	utloeserArbeidsgiveravgift: boolean
	fordel: string
	beskrivelse: string
}

type data = {
	formMethods: UseFormReturn
	inntektsinformasjonPath: string
}

const simpleValues = {
	inntektstype: 'LOENNSINNTEKT',
	inngaarIGrunnlagForTrekk: true,
	utloeserArbeidsgiveravgift: true,
	fordel: 'kontantytelse',
	beskrivelse: 'fastloenn',
}

export const InntektForm = ({ formMethods, inntektsinformasjonPath }: data) => {
	const [formSimple, setFormSimple] = useBoolean(
		sessionStorage.getItem(INNTEKTSTYPE_FORENKLET_TOGGLE) === FormType.FORENKLET,
	)

	useEffect(() => {
		formSimple && changeFormType(FormType.FORENKLET)
	}, [formSimple])

	const changeFormType = (type: FormType) => {
		const eventValueSimple = type === FormType.FORENKLET
		sessionStorage.setItem(INNTEKTSTYPE_FORENKLET_TOGGLE, type)
		setFormSimple(eventValueSimple)

		const restValues = eventValueSimple && { ...simpleValues }

		const inntektsListe = formMethods.watch(`${inntektsinformasjonPath}.inntektsliste`)
		const newInntektArray =
			inntektsListe &&
			inntektsListe.map((inntekt: inntekt) => ({
				beloep: inntekt.beloep,
				startOpptjeningsperiode: inntekt.startOpptjeningsperiode,
				sluttOpptjeningsperiode: inntekt.sluttOpptjeningsperiode,
				...restValues,
			}))

		newInntektArray &&
			formMethods.setValue(`${inntektsinformasjonPath}.inntektsliste`, newInntektArray)
		formMethods.trigger(`${inntektsinformasjonPath}.inntektsliste`)
	}

	return (
		<>
			<div className="toggle--wrapper">
				<ToggleGroup
					defaultValue={formSimple ? FormType.FORENKLET : FormType.STANDARD}
					onChange={(value: FormType) => changeFormType(value)}
					size={'small'}
					style={{ backgroundColor: '#ffffff' }}
				>
					<ToggleGroup.Item value={FormType.STANDARD}>Standard</ToggleGroup.Item>
					<ToggleGroup.Item value={FormType.FORENKLET}>Forenklet</ToggleGroup.Item>
				</ToggleGroup>
			</div>
			<FormDollyFieldArray
				name={`${inntektsinformasjonPath}.inntektsliste`}
				header="Inntekt per måned"
				newEntry={formSimple ? simpleInitialValues : initialValues}
				tag={null}
			>
				{(path: string) => (
					<>
						<FormTextInput name={`${path}.beloep`} label="Beløp" type="number" />
						<FormDatepicker
							name={`${path}.startOpptjeningsperiode`}
							label="Start opptjeningsperiode"
						/>
						<FormDatepicker
							name={`${path}.sluttOpptjeningsperiode`}
							label="Slutt opptjeningsperiode"
						/>
						{!formSimple && <InntektStub inntektPath={path} />}
					</>
				)}
			</FormDollyFieldArray>
		</>
	)
}

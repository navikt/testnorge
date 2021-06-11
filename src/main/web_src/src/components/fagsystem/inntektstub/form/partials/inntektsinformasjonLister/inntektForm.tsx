import React, { SyntheticEvent } from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import InntektStub from '~/components/inntektStub/validerInntekt'
import { useBoolean } from 'react-use'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikProps } from 'formik'
import _get from 'lodash/get'

const initialValues = {
	beloep: '',
	startOpptjeningsperiode: '',
	sluttOpptjeningsperiode: '',
	inntektstype: ''
}

const simpleInitialValues = {
	beloep: '',
	startOpptjeningsperiode: '',
	sluttOpptjeningsperiode: '',
	inntektstype: 'LOENNSINNTEKT',
	inngaarIGrunnlagForTrekk: true,
	utloeserArbeidsgiveravgift: true,
	fordel: 'kontantytelse',
	beskrivelse: 'fastloenn'
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
	formikBag: FormikProps<{}>
	inntektsinformasjonPath: string
}

const simpleValues = {
	inntektstype: 'LOENNSINNTEKT',
	inngaarIGrunnlagForTrekk: true,
	utloeserArbeidsgiveravgift: true,
	fordel: 'kontantytelse',
	beskrivelse: 'fastloenn'
}

export const InntektForm = ({ formikBag, inntektsinformasjonPath }: data) => {
	const [formSimple, setFormSimple] = useBoolean(false)

	const changeFormType = (event: SyntheticEvent<EventTarget, Event>) => {
		// @ts-ignore
		const eventValueSimple = event.target?.value.includes('forenklet')
		setFormSimple(eventValueSimple)

		const restValues = eventValueSimple && { ...simpleValues }

		const inntektsListe = _get(formikBag.values, `${inntektsinformasjonPath}.inntektsliste`)
		const newInntektArray =
			inntektsListe &&
			inntektsListe.map((inntekt: inntekt) => ({
				beloep: inntekt.beloep,
				startOpptjeningsperiode: inntekt.startOpptjeningsperiode,
				sluttOpptjeningsperiode: inntekt.sluttOpptjeningsperiode,
				...restValues
			}))

		newInntektArray &&
			formikBag.setFieldValue(`${inntektsinformasjonPath}.inntektsliste`, newInntektArray)
	}
	return (
		<>
			<div className="toggle--wrapper">
				<ToggleGruppe onChange={changeFormType} name="toggler">
					<ToggleKnapp value="standard" checked={!formSimple}>
						Standard
					</ToggleKnapp>
					<ToggleKnapp value="forenklet" checked={formSimple}>
						Forenklet
					</ToggleKnapp>
				</ToggleGruppe>
			</div>
			<FormikDollyFieldArray
				name={`${inntektsinformasjonPath}.inntektsliste`}
				header="Inntekt"
				newEntry={formSimple ? simpleInitialValues : initialValues}
				tag={null}
			>
				{(path: string) => (
					<>
						<FormikTextInput name={`${path}.beloep`} label="Beløp" type="number" />
						<FormikDatepicker
							name={`${path}.startOpptjeningsperiode`}
							label="Start opptjeningsperiode"
						/>
						<FormikDatepicker
							name={`${path}.sluttOpptjeningsperiode`}
							label="Slutt opptjeningsperiode"
						/>
						{!formSimple && <InntektStub formikBag={formikBag} inntektPath={path} />}
					</>
				)}
			</FormikDollyFieldArray>
		</>
	)
}

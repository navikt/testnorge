import React, { useState } from 'react'
import { FieldArray, Field } from 'formik'
import * as Yup from 'yup'
import _get from 'lodash/get'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { InntektsaarForm } from './inntektsaarForm'
import { VisAlleInntektsaar } from './visAlleInntektsaar'

export const SigrunstubForm = ({ formikBag }) => {
	const [alleInntekter, setAlleInntekter] = useState([])
	return (
		<React.Fragment>
			<Panel heading="Inntekt" hasErrors={panelError(formikBag)}>
				<InntektsaarForm alleInntekter={alleInntekter} setAlleInntekter={setAlleInntekter} />

				<button onClick={() => leggTilInntektsaar(formikBag, alleInntekter, setAlleInntekter)}>
					Legg til år
				</button>
				{/* Vis frem formikBag */}
				<VisAlleInntektsaar formikBag={formikBag} />
			</Panel>
		</React.Fragment>
	)
}

SigrunstubForm.initialValues = {
	sigrunstub: [
		{
			grunnlag: [],
			inntektsaar: '',
			svalbardGrunnlag: [],
			tjeneste: ''
		}
	]
}

SigrunstubForm.validation = {
	sigrunstub: Yup.object({
		grunnlag: [
			{
				tekniskNavn: Yup.string().required('Velg en type inntekt.'),
				verdi: Yup.number()
					.min(0, 'Tast inn et gyldig beløp')
					.required('Oppgi beløpet')
			}
		],
		inntektsaar: Yup.number()
			.integer('Ugyldig årstall')
			.required('Tast inn et gyldig år')
			.min(1968, 'Inntektsår må være 1968 eller senere')
			.max(2100, 'Inntektsår må være tidligere enn 2100'),
		svalbardGrunnlag: [
			{
				tekniskNavn: Yup.string().required('Velg en type inntekt.'),
				verdi: Yup.number()
					.min(0, 'Tast inn et gyldig beløp')
					.required('Oppgi beløpet')
			}
		],
		tjeneste: Yup.string('Velg en type tjeneste.')
	})
}

const leggTilInntektsaar = (formikBag, alleInntekter, setAlleInntekter) => {
	//TODO: Finne en bedre måte å hente counts på (idx og kdx)
	const idx =
		formikBag.values.sigrunstub[0].grunnlag.length === 0 &&
		formikBag.values.sigrunstub[0].svalbardGrunnlag.length === 0
			? 0
			: formikBag.values.sigrunstub.length

	formikBag.setFieldValue(`sigrunstub[${idx}].inntektsaar`, alleInntekter[0].inntektsaar)
	formikBag.setFieldValue(`sigrunstub[${idx}].tjeneste`, alleInntekter[0].tjeneste)

	let grunnlagCount = -1
	let svalbardGrunnlagCount = -1

	alleInntekter.forEach((inntekt, jdx) => {
		let grunnlag = ''
		let kdx = ''

		if (inntekt.inntektssted === 'Svalbard') {
			grunnlag = 'svalbardGrunnlag'
			svalbardGrunnlagCount++
			kdx = svalbardGrunnlagCount
		} else {
			grunnlag = 'grunnlag'
			grunnlagCount++
			kdx = grunnlagCount
		}

		formikBag.setFieldValue(
			`sigrunstub[${idx}].${grunnlag}[${kdx}].tekniskNavn`,
			inntekt.tekniskNavn
		)
		formikBag.setFieldValue(`sigrunstub[${idx}].${grunnlag}[${kdx}].verdi`, inntekt.verdi)
	})

	setAlleInntekter([])
}

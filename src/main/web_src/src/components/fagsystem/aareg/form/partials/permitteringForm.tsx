import React from 'react'
import _get from 'lodash/get'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialPermittering } from '../initialValues'
import { FormikProps } from 'formik'
import { AaregListe, Amelding } from '~/components/fagsystem/aareg/AaregTypes'

type Permittering = {
	path: string
	ameldingIndex: number
	arbeidsforholdIndex: number
	formikBag: FormikProps<{ aareg: AaregListe }>
	erLenket: boolean
}

const infotekst = 'Start- og sluttdato må være innenfor perioden til arbeidsforholdet'

export const PermitteringForm = ({
	path,
	ameldingIndex,
	arbeidsforholdIndex,
	formikBag,
	erLenket,
	onChangeLenket
}: Permittering) => {
	const maaneder = _get(formikBag.values, 'aareg[0].amelding')

	const handleNewEntry = () => {
		if (!maaneder) return
		maaneder.forEach((maaned: Amelding, idMaaned: number) => {
			if (!erLenket && idMaaned != ameldingIndex) return
			const currPermittering = _get(
				formikBag.values,
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].permittering`
			)
			formikBag.setFieldValue(
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].permittering`,
				currPermittering ? [...currPermittering, initialPermittering] : [initialPermittering]
			)
		})
	}

	const handleRemoveEntry = (idPermittering: number) => {
		if (!maaneder) return
		maaneder.forEach((maaned: Amelding, idMaaned: number) => {
			if (!erLenket && idMaaned != ameldingIndex) return
			const currPermittering = _get(
				formikBag.values,
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].permittering`
			)
			currPermittering.splice(idPermittering, 1)
			formikBag.setFieldValue(
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].permittering`,
				currPermittering
			)
		})
	}

	//! Har disablet changehandlers fordi de mest sannsynlig ikke er nødvendige, og de kompliserer skjemaet mye.
	//! Lar dem bli liggende en stund i tilfelle det blir behov for dem.

	return (
		<FormikDollyFieldArray
			name={path}
			header="Permittering"
			hjelpetekst={infotekst}
			newEntry={initialPermittering}
			nested
			// handleNewEntry={maaneder ? handleNewEntry : null}
			// handleRemoveEntry={maaneder ? handleRemoveEntry : null}
		>
			{(path: string, idx: number) => (
				<React.Fragment key={idx}>
					<FormikDatepicker
						name={`${path}.permitteringsPeriode.fom`}
						label="Permittering fra"
						// onChange={onChangeLenket(`permittering[${idx}].permitteringsPeriode.fom`)}
					/>
					<FormikDatepicker
						name={`${path}.permitteringsPeriode.tom`}
						label="Permittering til"
						// onChange={onChangeLenket(`permittering[${idx}].permitteringsPeriode.tom`)}
					/>
					<FormikTextInput
						name={`${path}.permitteringsprosent`}
						label="Permitteringsprosent"
						type="number"
						// onChange={onChangeLenket(`permittering[${idx}].permitteringsprosent`)}
					/>
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}

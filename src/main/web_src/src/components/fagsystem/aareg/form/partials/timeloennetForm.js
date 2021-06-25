import React from 'react'
import _get from 'lodash/get'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialTimeloennet } from '../initialValues'

const infotekst =
	'Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til arbeidsforholdet'

export const TimeloennetForm = ({
	path,
	ameldingIndex,
	arbeidsforholdIndex,
	formikBag,
	erLenket,
	onChangeLenket
}) => {
	const maaneder = _get(formikBag.values, 'aareg[0].amelding')

	const handleNewEntry = () => {
		if (!maaneder) return
		maaneder.forEach((maaned, idMaaned) => {
			if (!erLenket && idMaaned != ameldingIndex) return
			const currTimeloennet = _get(
				formikBag.values,
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].antallTimerForTimeloennet`
			)
			formikBag.setFieldValue(
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].antallTimerForTimeloennet`,
				currTimeloennet ? [...currTimeloennet, initialTimeloennet] : [initialTimeloennet]
			)
		})
	}

	const handleRemoveEntry = idTimeloennet => {
		if (!maaneder) return
		maaneder.forEach((maaned, idMaaned) => {
			if (!erLenket && idMaaned != ameldingIndex) return
			const currTimeloennet = _get(
				formikBag.values,
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].antallTimerForTimeloennet`
			)
			currTimeloennet.splice(idTimeloennet, 1)
			formikBag.setFieldValue(
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].antallTimerForTimeloennet`,
				currTimeloennet
			)
		})
	}

	return (
		<FormikDollyFieldArray
			name={path}
			header="Timer med timelønnet"
			hjelpetekst={infotekst}
			newEntry={initialTimeloennet}
			nested
			handleNewEntry={maaneder ? handleNewEntry : null}
			handleRemoveEntry={maaneder ? handleRemoveEntry : null}
		>
			{(path, idx) => (
				<div key={idx} className="flexbox">
					<FormikTextInput
						name={`${path}.antallTimer`}
						label="Antall timer for timelønnet"
						type="number"
						onChange={onChangeLenket(`antallTimerForTimeloennet[${idx}].antallTimer`)}
					/>
					<FormikDatepicker
						name={`${path}.periode.fom`}
						label="Periode fra"
						onChange={onChangeLenket(`antallTimerForTimeloennet[${idx}].periode.fom`)}
					/>
					<FormikDatepicker
						name={`${path}.periode.tom`}
						label="Periode til"
						onChange={onChangeLenket(`antallTimerForTimeloennet[${idx}].periode.tom`)}
					/>
				</div>
			)}
		</FormikDollyFieldArray>
	)
}

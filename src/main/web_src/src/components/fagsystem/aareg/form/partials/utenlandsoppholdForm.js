import React from 'react'
import _get from 'lodash/get'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialUtenlandsopphold } from '../initialValues'

const infotekst =
	'Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til arbeidsforholdet'

export const UtenlandsoppholdForm = ({
	path,
	ameldingIndex,
	arbeidsforholdIndex,
	formikBag,
	erLenket
}) => {
	const maaneder = _get(formikBag.values, 'aareg[0].amelding')

	const handleNewEntry = () => {
		if (!maaneder) return
		maaneder.forEach((maaned, idMaaned) => {
			if (!erLenket && idMaaned != ameldingIndex) return
			const currUtenlandsopphold = _get(
				formikBag.values,
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].utenlandsopphold`
			)
			formikBag.setFieldValue(
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].utenlandsopphold`,
				currUtenlandsopphold
					? [...currUtenlandsopphold, initialUtenlandsopphold]
					: [initialUtenlandsopphold]
			)
		})
	}

	const handleRemoveEntry = idUtenlandsopphold => {
		if (!maaneder) return
		maaneder.forEach((maaned, idMaaned) => {
			if (!erLenket && idMaaned != ameldingIndex) return
			const currUtenlandsopphold = _get(
				formikBag.values,
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].utenlandsopphold`
			)
			currUtenlandsopphold.splice(idUtenlandsopphold, 1)
			formikBag.setFieldValue(
				`aareg[0].amelding[${idMaaned}].arbeidsforhold[${arbeidsforholdIndex}].utenlandsopphold`,
				currUtenlandsopphold
			)
		})
	}

	return (
		<FormikDollyFieldArray
			name={path}
			header="Utenlandsopphold"
			hjelpetekst={infotekst}
			newEntry={initialUtenlandsopphold}
			nested
			handleNewEntry={maaneder ? handleNewEntry : null}
			handleRemoveEntry={maaneder ? handleRemoveEntry : null}
		>
			{(path, idx) => (
				<div key={idx} className="flexbox">
					<FormikSelect
						name={`${path}.land`}
						label="Land"
						kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
						isClearable={false}
						size="large"
					/>
					<FormikDatepicker name={`${path}.periode.fom`} label="Opphold fra" />
					<FormikDatepicker name={`${path}.periode.tom`} label="Opphold til" />
				</div>
			)}
		</FormikDollyFieldArray>
	)
}

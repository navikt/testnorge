import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'

const datoOverlapperIkkeAndreOppholdTest = (oppholdValidation, validerStart) => {
	const errorMsgAge =
		'Startdato må være før sluttdato og tidsrommet for et opphold kan ikke overlappe et annet.'

	return oppholdValidation.test('range', errorMsgAge, (val, testContext) => {
		if (!val && validerStart) return true
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value

		const selectedDate = new Date(val)
		const arrayPos = parseInt(testContext.path.match(/\d+/)[0])
		const alleOpphold = fullForm.instdata || []

		for (let i = 0; i < alleOpphold.length; i++) {
			const sluttDatoValue = alleOpphold[i].sluttdato
			const startDatoValue = alleOpphold[i].startdato
			const sluttDato = new Date(sluttDatoValue)
			const startDato = new Date(startDatoValue)

			if (validerStart) {
				if (i === arrayPos) {
					return !(sluttDatoValue !== '' && selectedDate >= sluttDato)
				} else {
					if (sluttDatoValue !== '') {
						if (selectedDate < sluttDato && selectedDate >= startDato) return false
					} else if (selectedDate >= startDato) return false
				}
			} else {
				const selectedStartValue = alleOpphold[arrayPos]?.startdato
				if (!val) {
					if (i < arrayPos && selectedStartValue !== '') {
						if (sluttDatoValue === '') return false
					}
				} else if (i === arrayPos) {
					return !(startDatoValue !== '' && selectedDate <= startDato)
				} else {
					if (sluttDatoValue !== '') {
						if (selectedDate <= sluttDato && selectedDate > startDato) return false
						if (selectedDate >= sluttDato && new Date(selectedStartValue) <= startDato) return false
					} else if (selectedDate > startDato) return false
				}
			}
		}
		return true
	})
}

export const validation = {
	instdata: ifPresent(
		'$instdata',
		Yup.array().of(
			Yup.object({
				institusjonstype: requiredString,
				startdato: datoOverlapperIkkeAndreOppholdTest(requiredDate, true),
				forventetSluttdato: Yup.string().nullable(),
				sluttdato: datoOverlapperIkkeAndreOppholdTest(Yup.string().nullable(), false),
			}),
		),
	),
}

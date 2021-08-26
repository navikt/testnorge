import * as Yup from 'yup'
import { requiredDate, requiredString } from '~/utils/YupValidations'
import _get from 'lodash/get'

const datoOverlapperIkkeAndreOppholdTest = (validation, validerStart) => {
	const errorMsgAge =
		'Startdato må være før sluttdato og tidsrommet for et opphold kan ikke overlappe et annet.'

	return validation.test('range', errorMsgAge, function isWithinTest(val) {
		if (!val && validerStart) return true

		const selectedDate = new Date(val)
		const path = this.path.split('[')[0]
		const values = this.options.context
		const arrayPos = parseInt(this.path.match(/\d+/)[0])
		const alleOpphold = _get(values, `${path}`)

		for (let i = 0; i < alleOpphold.length; i++) {
			const sluttDatoValue = _get(values, `${path}[${i}].sluttdato`)
			const startDatoValue = _get(values, `${path}[${i}].startdato`)
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
				const selectedStartValue = _get(values, `${path}[${arrayPos}].startdato`)
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
	instdata: Yup.array().of(
		Yup.object({
			institusjonstype: requiredString,
			startdato: datoOverlapperIkkeAndreOppholdTest(requiredDate, true),
			sluttdato: datoOverlapperIkkeAndreOppholdTest(Yup.string().nullable(), false)
		})
	)
}

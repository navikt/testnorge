import * as Yup from 'yup'
import { requiredDate, requiredString } from '~/utils/YupValidations'
import _get from 'lodash/get'

const datoOverlapperIkkeAndreOppholdTest = (validation, validerStart) => {
	const errorMsgAge =
		'Startdato må være før sluttdato og tidsrommet for et opphold kan ikke overlappe et annet.'

	return validation.test(
		'range',
		errorMsgAge,
		function isWithinTest(val) {
			if (!val) return true

			const selectedDate = new Date(val)
			const path = this.path.split('[')[0]
			const values = this.options.context
			const arrayPos = parseInt(this.path.match(/\d+/)[0])
			const alleOpphold = _get(values, `${path}`)

			for (let i = 0; i < alleOpphold.length; i++) {
				const sluttDatoValue = _get(values, `${path}[${i}].faktiskSluttdato`)
				const startDatoValue = _get(values, `${path}[${i}].startdato`)
				const sluttDato = new Date(sluttDatoValue)
				const startDato = new Date(startDatoValue)

				if(validerStart){
					if (i === arrayPos) {
						if (sluttDatoValue!=='' && selectedDate >= sluttDato) return false
					}else{
						if (sluttDatoValue!=='') {
							if (selectedDate < sluttDato && selectedDate >= startDato) return false
						} else if (selectedDate >= startDato) return false
					}
				}else{
					if (i === arrayPos) {
						if (startDatoValue!=='' && selectedDate <= startDato) return false
					} else {
						if (sluttDatoValue!=='') {
							if (selectedDate <= sluttDato && selectedDate > startDato) return false
						} else if (selectedDate > startDato) return false
					}
				}
			}
			return true
		}
	)
}

export const validation = {
	instdata: Yup.array().of(
		Yup.object({
			institusjonstype: requiredString,
			startdato: datoOverlapperIkkeAndreOppholdTest(requiredDate, true),
			faktiskSluttdato: datoOverlapperIkkeAndreOppholdTest(Yup.string().nullable(), false)
		})
	)
}

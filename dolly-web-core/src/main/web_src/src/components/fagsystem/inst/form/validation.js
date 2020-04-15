import * as Yup from 'yup'
import { requiredDate, requiredString } from '~/utils/YupValidations'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'

const datoOverlapperIkkeAndreOppholdTest = (validation, validerStart) => {
	const errorMsgAge =
		'Startdato må være før sluttdato og tidsrommet for et opphold kan ikke overlappe et annet.'

	return validation.test(
		'range',
		errorMsgAge,
		function isWithinTest(val) {
			if ( !val ) return true

			const dateValue = new Date(val)
			const path = this.path.split('[')[0]
			const values = this.options.context
			const arrayPos = parseInt(this.path.match(/\d+/)[0]);
			const alleOpphold = _get(values, `${path}`)

			for (let i = 0; i < alleOpphold.length; i++) {
				const sluttDatoValue = _get(values, `${path}[${i}].faktiskSluttdato`)
				const startDatoValue = _get(values, `${path}[${i}].startdato`)
				const sluttDato = new Date(sluttDatoValue)
				const startDato = new Date(startDatoValue)

				if(i===arrayPos){
					if(validerStart){
						if (sluttDatoValue!=='' && dateValue > sluttDato) return false
					}else{
						if(!_isNil(startDatoValue) && dateValue < startDato) return false
					}

				}else{
					if (sluttDatoValue!=='') {
						if(dateValue < sluttDato && dateValue > startDato) return false
					}else if(validerStart){
						if(dateValue >= startDato) return false
					}else if(dateValue > startDato) return false
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

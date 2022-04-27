import * as Yup from 'yup'
import { ifPresent } from '~/utils/YupValidations'

export const validation = {
	pensjonforvalter: ifPresent(
		'$pensjonforvalter',
		Yup.object({
			tp: Yup.object({
				ordning: Yup.string()
					.min(4, 'Tast inn et gyldig TP nummer')
					.max(4, 'Tast inn et gyldig TP nummer')
					.typeError('Tast inn et gyldig TP nummer'),
			}),
		})
	),
}

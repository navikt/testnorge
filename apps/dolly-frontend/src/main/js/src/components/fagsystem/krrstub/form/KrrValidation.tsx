import * as Yup from 'yup'
import { ifPresent, requiredBoolean } from '@/utils/YupValidations'

export const KrrValidation = {
	krrstub: ifPresent(
		'$krrstub',
		Yup.object({
			epost: Yup.string(),
			gyldigFra: Yup.date().nullable(),
			mobil: Yup.string().matches(/^\+?\d{8,14}$/, {
				message: 'Ugyldig telefonnummer',
				excludeEmptyString: true,
			}),
			sdpAdresse: Yup.string(),
			sdpLeverandoer: Yup.string().nullable(),
			spraak: Yup.string().notRequired().nullable(),
			registrert: ifPresent('$krrstub.registrert', requiredBoolean),
			reservert: Yup.boolean().nullable(),
		}),
	),
}

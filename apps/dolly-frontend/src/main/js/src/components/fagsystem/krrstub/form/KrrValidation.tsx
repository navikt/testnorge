import * as Yup from 'yup'
import { ifPresent, requiredBoolean } from '@/utils/YupValidations'

export const KrrValidation = {
	krrstub: Yup.object({
		epost: Yup.string(),
		gyldigFra: Yup.date().nullable(),
		mobil: Yup.string().matches(/^\+?\d*$/, 'Ugyldig mobilnummer'),
		sdpAdresse: Yup.string(),
		sdpLeverandoer: Yup.string().nullable(),
		spraak: Yup.string(),
		registrert: ifPresent('$krrstub.registrert', requiredBoolean),
		reservert: Yup.boolean().nullable(),
	}),
}

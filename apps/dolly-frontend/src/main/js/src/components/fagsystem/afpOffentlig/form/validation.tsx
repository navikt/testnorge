import { ifPresent, requiredDate, requiredNumber } from '@/utils/YupValidations'
import * as Yup from 'yup'

export const validation = {
	afpOffentlig: ifPresent(
		'$pensjonforvalter.afpOffentlig',
		Yup.object({
			direktekall: Yup.array().of(Yup.string()).nullable(),
			mocksvar: Yup.array().of(
				Yup.object({
					tpId: Yup.string().nullable(),
					statusAfp: Yup.string().nullable(),
					virkningsDato: Yup.date().nullable(),
					sistBenyttetG: Yup.number()
						.transform((i, j) => (j === '' ? null : i))
						.nullable(),
					belopsListe: Yup.array().of(
						Yup.object({
							fomDato: ifPresent('$fomDato', requiredDate),
							belop: ifPresent(
								'$belop',
								requiredNumber.transform((i, j) => (j === '' ? null : i)),
							),
						}),
					),
				}),
			),
		}),
	),
}

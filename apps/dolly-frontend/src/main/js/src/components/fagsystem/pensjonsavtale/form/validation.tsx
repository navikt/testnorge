import * as Yup from 'yup'
import { ifPresent } from '@/utils/YupValidations'

export const validation = {
	tp: ifPresent(
		'$pensjonforvalter.pensjonsavtale',
		Yup.array().of(
			Yup.object({
				produktBetegnelse: Yup.string().required().typeError('Feltet er påkrevd'),
				avtaleKategori: Yup.string().required().typeError('Feltet er påkrevd'),
				startAlderAar: Yup.number().min(17).max(72),
				sluttAlderAar: Yup.number().min(17).max(72),
			}),
		),
	),
}

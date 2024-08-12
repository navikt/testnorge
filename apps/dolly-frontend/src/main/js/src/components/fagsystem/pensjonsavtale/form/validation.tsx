import * as Yup from 'yup'
import { ifPresent } from '@/utils/YupValidations'

export const validation = {
	pensjonsavtale: ifPresent(
		'$pensjonforvalter.pensjonsavtale',
		Yup.array().of(
			Yup.object({
				produktBetegnelse: Yup.string().required('Feltet er påkrevd'),
				avtaleKategori: Yup.string().required('Feltet er påkrevd'),
				utbetalingsperioder: Yup.array().of(
					Yup.object({
						startAlderAar: Yup.number()
							.min(62, 'Minimum 62 år')
							.max(72, 'Maksimum 72 år')
							.required('Feltet er påkrevd')
							.typeError('Feltet er påkrevd og må ha verdi nellom 62 og 72 år'),
						startAlderMaaned: Yup.number()
							.min(1, 'Minimum 1 (januar)')
							.max(12, 'Maksimum 12 (desember)')
							.required('Feltet er påkrevd')
							.typeError('Feltet er påkrevd og må ha verdi mellom 1 og 12 (januar-desember)'),
						sluttAlderAar: Yup.number()
							.nullable()
							.min(72, 'Minimum 72 år')
							.max(100, 'Maksimum 100 år')
							.transform((value) => (Number.isNaN(value) ? null : value))
							.test(
								'has-value-if-sluttAlderMaaned-is-stated',
								'Sluttalder År må være satt når Sluttalder Måned har verdi',
								(sluttAlderAar, context) => {
									return sluttAlderAar || (!sluttAlderAar && !context.parent.sluttAlderMaaned)
								},
							),
						sluttAlderMaaned: Yup.number()
							.nullable()
							.min(1, 'Minimum 1 (januar)')
							.max(12, 'Maksimum 12 (desember)')
							.typeError('Verdi kan angis, og må da ha verdi mellom 1 og 12 (januar-desember)')
							.test(
								'has-value-if-sluttAlderAar-is-stated',
								'Feltet er påkrevd når Sluttalder År er angitt',
								(sluttAlderMaaned, context) => {
									return sluttAlderMaaned || (!sluttAlderMaaned && !context.parent.sluttAlderAar)
								},
							),
						aarligUtbetaling: Yup.number()
							.required()
							.min(1000, 'Minimum 1000 kr')
							.typeError('Verdi kan angis, og må da ha verdi minimum 1000 kr'),
					}),
				),
			}),
		),
	),
}

import * as Yup from 'yup'
import { ifPresent } from '~/utils/YupValidations'
import { testDatoTom } from '~/components/fagsystem/pdlf/form/validation'

export const validation = {
	tp: ifPresent(
		'$pensjonforvalter.tp',
		Yup.array().of(
			Yup.object({
				ordning: Yup.string()
					.min(4, 'Tast inn et gyldig TP nummer')
					.max(4, 'Tast inn et gyldig TP nummer')
					.matches('^\\d+$', 'Tast inn et gyldig TP nummer')
					.typeError('Tast inn et gyldig TP nummer'),
				ytelser: Yup.array().of(
					Yup.object({
						type: Yup.string().required().typeError('Tast inn et gyldig TP nummer'),
						datoInnmeldtYtelseFom: Yup.string()
							.required()
							.typeError('Tast inn et gyldig innmeldt ytelse dato'),
						datoYtelseIverksattFom: testDatoTom(
							Yup.string(),
							'datoInnmeldtYtelseFom',
							'Tast inn Ytelse Fom dato som er etter innmeldt ytelse dato'
						)
							.required()
							.typeError('Tast inn et iverksatt Fom dato'),
						datoYtelseIverksattTom: testDatoTom(
							Yup.date().nullable(),
							'datoYtelseIverksattFom',
							'Tast inn Ytelse Tom dato som er etter Ytelse Fom dato eller fjern det'
						),
					})
				),
			})
		)
	),
}

import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'
import { testDatoTom } from '@/components/fagsystem/utils'

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
						type: requiredString,
						datoInnmeldtYtelseFom: requiredDate,
						datoYtelseIverksattFom: testDatoTom(
							requiredDate,
							'datoInnmeldtYtelseFom',
							'Velg ytelse f.o.m.-dato som er etter medlemskap f.o.m-dato'
						)
							.required()
							.typeError('Tast inn et iverksatt Fom dato'),
						datoYtelseIverksattTom: testDatoTom(
							Yup.date().nullable(),
							'datoYtelseIverksattFom',
							'Velg ytelse t.o.m.-dato som er etter ytelse f.o.m-dato eller fjern den'
						),
					})
				),
			})
		)
	),
}

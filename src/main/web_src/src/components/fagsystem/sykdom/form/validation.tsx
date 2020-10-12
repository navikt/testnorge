import * as Yup from 'yup'
import { requiredDate, requiredString, requiredNumber, ifPresent } from '~/utils/YupValidations'

export const validation = {
	sykemelding: ifPresent(
		'$sykemelding',
		Yup.object({
			syntSykemelding: ifPresent(
				'$sykemelding.syntSykemelding',
				Yup.object({
					startDato: requiredDate,
					orgnummer: requiredString
				})
			),
			detaljertSykemelding: ifPresent(
				'$sykemelding.detaljertSykemelding',
				Yup.object({
					startDato: requiredDate,
					hovedDiagnose: Yup.object({
						diagnose: requiredString,
						diagnosekode: requiredString
					}),
					biDiagnoser: Yup.array().of(
						ifPresent(
							'$sykemelding.detaljertSykemelding.biDiagnoser[0].diagnose',
							Yup.object({
								diagnose: requiredString,
								diagnosekode: requiredString
							})
						)
					),
					helsepersonell: Yup.object({
						etternavn: requiredString,
						fornavn: requiredString,
						ident: requiredString,
						hprId: requiredString
					}),
					arbeidsgiver: Yup.object({
						navn: requiredString,
						stillingsprosent: requiredNumber.transform(num => (isNaN(num) ? undefined : num)),
						yrkesbetegnelse: requiredString
					}),
					perioder: Yup.array().of(
						Yup.object({
							aktivitet: Yup.object({}),
							fom: requiredDate,
							tom: requiredDate
						})
					)
				})
			)
		})
	)
}

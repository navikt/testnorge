import * as Yup from 'yup'
import { ifPresent, requiredDate } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

const vanligAktivitetSchema = Yup.object({
	grad: Yup.number()
		.nullable()
		.transform((value, original) =>
			original === '' || original === null || original === undefined ? null : value,
		)
		.min(1, 'Grad må være mellom 1 og 99')
		.max(99, 'Grad må være mellom 1 og 99, tøm feltet for full sykemelding'),
	reisetilskudd: Yup.boolean()
		.nullable()
		.test(
			'reisetilskudd-requires-grad',
			'Reisetilskudd krever at grad er satt',
			function (value) {
				if (!value) return true
				const grad = this.parent?.grad
				return grad !== null && grad !== undefined && grad !== ''
			},
		),
	fom: testDatoFom(requiredDate, 'tom'),
	tom: testDatoTom(requiredDate, 'fom'),
})

const enAktivitetSchema = Yup.object({
	fom: testDatoFom(requiredDate, 'tom'),
	tom: testDatoTom(requiredDate, 'fom'),
})

export const validation = {
	sykemelding: ifPresent(
		'$sykemelding',
		Yup.object({
			nySykemelding: ifPresent(
				'$sykemelding.nySykemelding',
				Yup.object({
					type: Yup.string()
						.required('Type sykemelding er påkrevd')
						.oneOf(
							['VANLIG', 'AVVENTENDE', 'BEHANDLINGSDAGER', 'REISETILSKUDD'],
							'Ugyldig type sykemelding',
						),
					aktivitet: Yup.array()
						.min(1, 'Må ha minst én aktivitet')
						.when('type', {
							is: 'VANLIG',
							then: (schema) => schema.of(vanligAktivitetSchema),
							otherwise: (schema) =>
								schema
									.max(1, 'Kun én aktivitet er tillatt for denne typen sykemelding')
									.of(enAktivitetSchema),
						}),
				}),
			),
		}),
	),
}

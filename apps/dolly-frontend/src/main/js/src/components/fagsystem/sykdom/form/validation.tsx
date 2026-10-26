import * as Yup from 'yup'
import { ifPresent, requiredDate } from '@/utils/YupValidations'
import { isAfter, startOfDay } from 'date-fns'

const fomFoerTom = (fom: any, tom: any) => {
	if (!fom || !tom) return true
	return !isAfter(startOfDay(new Date(fom)), startOfDay(new Date(tom)))
}

const fomDato = requiredDate.test(
	'is-before-tom',
	'Dato må være før eller lik til-dato',
	function (value) {
		return fomFoerTom(value, this.parent?.tom)
	},
)

const tomDato = requiredDate.test(
	'is-after-fom',
	'Dato må være etter eller lik fra-dato',
	function (value) {
		return fomFoerTom(this.parent?.fom, value)
	},
)

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
		.test('reisetilskudd-requires-grad', 'Reisetilskudd krever at grad er satt', function (value) {
			if (!value) return true
			const grad = this.parent?.grad
			return grad !== null && grad !== undefined && grad !== ''
		}),
	fom: fomDato,
	tom: tomDato,
})

const enAktivitetSchema = Yup.object({
	fom: fomDato,
	tom: tomDato,
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

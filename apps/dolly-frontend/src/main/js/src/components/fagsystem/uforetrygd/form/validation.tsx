import * as Yup from 'yup'
import { ifPresent, requiredNumber, requiredString } from '@/utils/YupValidations'
import { isAfter, isBefore, isFuture, isPast } from 'date-fns'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

const erIkkeLik = () => {
	return Yup.string().test(
		'er-ikke-lik',
		'Saksbehandler og attesterer kan ikke være samme person',
		(_value, testContext) => {
			const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
			const { saksbehandler, attesterer } = fullForm?.pensjonforvalter?.uforetrygd
			if (!saksbehandler || !attesterer) {
				return true
			}
			return saksbehandler !== attesterer
		},
	)
}

const datoErFremtidig = () => {
	return Yup.date().test('er-fremtidig', 'Dato må være frem i tid', function validDate(dato) {
		if (!dato) {
			return true
		}
		return isFuture(dato)
	})
}

const forventetInntekt = Yup.object({
	datoFom: testDatoFom(datoErFremtidig().nullable(), 'datoTom', 'Dato må være før dato t.o.m.'),
	datoTom: testDatoTom(datoErFremtidig().nullable(), 'datoFom', 'Dato må være etter dato f.o.m.'),
	inntektType: Yup.string().nullable(),
	belop: Yup.number()
		.nullable()
		.transform((i, j) => (j === '' ? null : i)),
}).nullable()

export const validation = {
	uforetrygd: ifPresent(
		'$pensjonforvalter.uforetrygd',
		Yup.object({
			uforetidspunkt: Yup.date()
				.test('er-historisk', 'Dato må være historisk', function validDate(dato) {
					return isPast(dato)
				})
				.nullable(),
			kravFremsattDato: Yup.date()
				.test(
					'er-foer-virkningsdato',
					'Dato må være etter uføretidspunkt og før ønsket virkningsdato',
					(kravFremsattDato, testContext) => {
						const context = testContext.options.context
						const fullForm =
							testContext.from && testContext.from[testContext.from.length - 1]?.value
						const virkningsdato = fullForm?.pensjonforvalter?.uforetrygd?.onsketVirkningsDato
						const uforetidspunkt = fullForm?.pensjonforvalter?.uforetrygd?.uforetidspunkt
						return (
							isBefore(new Date(kravFremsattDato), new Date(virkningsdato)) &&
							isAfter(new Date(kravFremsattDato), new Date(uforetidspunkt))
						)
					},
				)
				.nullable(),
			onsketVirkningsDato: Yup.date()
				.test(
					'er-foer-virkningsdato',
					'Dato må være etter dato for fremsettelse av krav (og tidligst 1. januar 2015)',
					(onsketVirkningsDato, testContext) => {
						const fullForm =
							testContext.from && testContext.from[testContext.from.length - 1]?.value
						const kravFremsattDato = fullForm?.pensjonforvalter?.uforetrygd?.kravFremsattDato
						return (
							isAfter(new Date(onsketVirkningsDato), new Date(kravFremsattDato)) &&
							isAfter(new Date(onsketVirkningsDato), new Date('2015-01-01'))
						)
					},
				)
				.nullable(),
			inntektForUforhet: requiredNumber.transform((i, j) => (j === '' ? null : i)),
			uforegrad: requiredNumber.transform((i, j) => (j === '' ? null : i)),
			minimumInntektForUforhetType: Yup.string().nullable(),
			saksbehandler: erIkkeLik().nullable(),
			attesterer: erIkkeLik().nullable(),
			navEnhetId: Yup.string().nullable(),
			barnetilleggDetaljer: Yup.object({
				barnetilleggType: requiredString,
				forventedeInntekterSoker: Yup.array().of(forventetInntekt),
				forventedeInntekterEP: Yup.array().of(forventetInntekt),
			}).nullable(),
		}),
	),
}

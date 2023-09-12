import * as Yup from 'yup'
import { ifPresent, requiredNumber, requiredString } from '@/utils/YupValidations'
import { isFuture, isPast } from 'date-fns'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

const erIkkeLik = () => {
	return Yup.string().test(
		'er-ikke-lik',
		'Saksbehandler og attesterer kan ikke være samme person',
		function testIdenter() {
			const values = this.options.context
			const { saksbehandler, attesterer } = values?.pensjonforvalter?.uforetrygd
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
	datoFom: testDatoFom(datoErFremtidig().nullable(), 'datoTom', 'Dato må være før dato t.o.m.'), // Før datoTom, frem i tid???
	datoTom: testDatoTom(datoErFremtidig().nullable(), 'datoFom', 'Dato må være etter dato f.o.m.'), // Etter datoFom, frem i tid???
	inntektType: Yup.string().nullable(),
	belop: Yup.number()
		.nullable()
		.transform((i, j) => (j === '' ? null : i)),
}).nullable()

export const validation = {
	uforetrygd: ifPresent(
		'$pensjonforvalter.uforetrygd',
		Yup.object({
			kravFremsattDato: Yup.date().nullable(),
			onsketVirkningsDato: datoErFremtidig().nullable(), // Frem i tid
			uforetidspunkt: Yup.date()
				.test('er-historisk', 'Dato må være historisk', function validDate(dato) {
					if (!dato) {
						return true
					}
					return isPast(dato)
				})
				.nullable(), // Historisk
			inntektForUforhet: requiredNumber.transform((i, j) => (j === '' ? null : i)), // Required
			uforegrad: requiredNumber.transform((i, j) => (j === '' ? null : i)), // Required
			minimumInntektForUforhetType: Yup.string().nullable(),
			saksbehandler: erIkkeLik().nullable(), // Kan ikke være lik attesterer
			attesterer: erIkkeLik().nullable(), // Kan ikke være lik saksbehandler
			navEnhetId: requiredString, // Required
			barnetilleggDetaljer: Yup.object({
				barnetilleggType: requiredString,
				forventedeInntekterSoker: Yup.array().of(forventetInntekt),
				forventedeInntekterEP: Yup.array().of(forventetInntekt),
			}).nullable(),
		}),
	),
}

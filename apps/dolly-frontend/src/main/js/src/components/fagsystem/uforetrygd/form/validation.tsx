import * as Yup from 'yup'
import { ifPresent, requiredNumber, requiredString } from '@/utils/YupValidations'
import { isBefore, isFuture, isPast } from 'date-fns'
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
			kravFremsattDato: Yup.date()
				.test(
					'er-foer-virkningsdato',
					'Dato må være før ønsket virkningsdato',
					function validDate(kravFremsattDato) {
						const virkningsdato =
							this.options.context?.pensjonforvalter?.uforetrygd?.onsketVirkningsDato
						return isBefore(new Date(kravFremsattDato), new Date(virkningsdato))
					},
				)
				.nullable(),
			onsketVirkningsDato: datoErFremtidig().nullable(),
			uforetidspunkt: Yup.date()
				.test('er-historisk', 'Dato må være historisk', function validDate(dato) {
					if (!dato) {
						return true
					}
					return isPast(dato)
				})
				.nullable(),
			inntektForUforhet: requiredNumber.transform((i, j) => (j === '' ? null : i)),
			uforegrad: requiredNumber.transform((i, j) => (j === '' ? null : i)),
			minimumInntektForUforhetType: Yup.string().nullable(),
			saksbehandler: erIkkeLik().nullable(),
			attesterer: erIkkeLik().nullable(),
			navEnhetId: requiredString,
			barnetilleggDetaljer: Yup.object({
				barnetilleggType: requiredString,
				forventedeInntekterSoker: Yup.array().of(forventetInntekt),
				forventedeInntekterEP: Yup.array().of(forventetInntekt),
			}).nullable(),
		}),
	),
}

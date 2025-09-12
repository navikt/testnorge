import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredNumber, requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

const testHarArbeidsforhold = (val) => {
	return val.test('har-arbeidsforhold', (selected, testContext) => {
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
		if (!selected) {
			return true
		}
		const detaljertSykemelding = fullForm?.sykemelding?.detaljertSykemelding

		const valgtArbeidsgiver = detaljertSykemelding
			? detaljertSykemelding?.mottaker?.orgNr
			: selected

		const arbeidsgivere = fullForm?.aareg?.map((arbforh) => arbforh?.arbeidsgiver?.orgnummer) || []

		fullForm?.personFoerLeggTil?.aareg?.forEach((miljo) => {
			miljo?.data?.forEach((arbforh) => {
				const orgnr = arbforh?.arbeidsgiver?.organisasjonsnummer
				if (orgnr && !arbeidsgivere?.includes(orgnr?.toString())) {
					arbeidsgivere.push(orgnr)
				}
			})
		})

		if (!arbeidsgivere?.includes(valgtArbeidsgiver?.toString())) {
			return testContext.createError({
				message: 'Personen må ha et arbeidsforhold i valgt organisasjon',
			})
		}

		return true
	})
}

export const validation = {
	sykemelding: ifPresent(
		'$sykemelding',
		Yup.object({
			syntSykemelding: ifPresent(
				'$sykemelding.syntSykemelding',
				Yup.object({
					startDato: requiredDate,
					orgnummer: testHarArbeidsforhold(Yup.string().nullable()),
				}),
			),
			detaljertSykemelding: ifPresent(
				'$sykemelding.detaljertSykemelding',
				Yup.object({
					startDato: requiredDate,
					hovedDiagnose: Yup.object({
						diagnose: requiredString,
						diagnosekode: requiredString,
					}),
					mottaker: Yup.object({
						navn: requiredString,
					}),
					organisasjon: Yup.object({
						arbeidsgiver: Yup.object({
							orgnummer: Yup.string()
								.required('Må ha gyldig organisasjonsnummer')
								.min(9, 'Orgnummer må være 9 siffer')
								.max(9, 'Orgnummer må være 9 siffer'),
						}),
					}),
					biDiagnoser: Yup.array().of(
						ifPresent(
							'$sykemelding.detaljertSykemelding.biDiagnoser[0].diagnose',
							Yup.object({
								diagnose: requiredString,
								diagnosekode: requiredString,
							}),
						),
					),
					helsepersonell: Yup.object({
						etternavn: requiredString,
						fornavn: requiredString,
						ident: requiredString,
						hprId: requiredString,
					}),
					arbeidsgiver: Yup.object({
						navn: testHarArbeidsforhold(Yup.string().required('Må ha gyldig arbeidsgiver')),
						stillingsprosent: requiredNumber.transform((num) => (isNaN(num) ? undefined : num)),
						yrkesbetegnelse: requiredString,
					}),
					perioder: Yup.array().of(
						Yup.object({
							aktivitet: Yup.object({}),
							fom: testDatoFom(requiredDate, 'tom'),
							tom: testDatoTom(requiredDate, 'fom'),
						}),
					),
				}),
			),
		}),
	),
}

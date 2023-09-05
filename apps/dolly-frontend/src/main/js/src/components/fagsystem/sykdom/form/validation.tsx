import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredNumber, requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

const testHarArbeidsforhold = (val) => {
	return val.test('har-arbeidsforhold', function harArbeidsforhold(selected) {
		if (!selected) {
			return true
		}
		const values = this?.options?.context
		const detaljertSykemelding = values?.sykemelding?.detaljertSykemelding

		const valgtArbeidsgiver = detaljertSykemelding
			? detaljertSykemelding?.mottaker?.orgNr
			: selected

		const arbeidsgivere = values?.aareg?.map((arbforh) => arbforh?.arbeidsgiver?.orgnummer)

		if (!arbeidsgivere?.includes(valgtArbeidsgiver?.toString())) {
			return this.createError({
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
						navn: testHarArbeidsforhold(Yup.string().nullable()),
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

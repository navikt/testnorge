import * as Yup from 'yup'
import { isBefore, addDays, areIntervalsOverlapping } from 'date-fns'
import { requiredDate, requiredString, requiredNumber, ifPresent } from '~/utils/YupValidations'

const overlappendePerioderTest = (dato, erTomDato) => {
	const errorMsg = erTomDato
		? 'Perioder kan ikke overlappe, og dato må være senere enn f.o.m. dato'
		: 'Perioder kan ikke overlappe'

	return dato.test('is-not-overlapping', errorMsg, function validDate(dato) {
		if (!dato) return true
		const values = this.options.context
		const path = this.options.path
		const index = parseInt(path.charAt(path.indexOf('[') + 1))
		const periodeListe = values.sykemelding.detaljertSykemelding.perioder
		let datoErGyldig = true

		if (erTomDato) {
			if (isBefore(new Date(dato), new Date(periodeListe[index].fom))) {
				return false
			}
		}

		if (periodeListe && periodeListe.length > 1 && index > 0) {
			periodeListe.map((periode, idx) => {
				if (
					index > idx &&
					areIntervalsOverlapping(
						{
							start: addDays(new Date(periode.fom), -1),
							end: addDays(new Date(periode.tom), 1)
						},
						erTomDato
							? {
									start: new Date(periodeListe[index].fom || dato),
									end: new Date(dato)
							  }
							: {
									start: new Date(dato),
									end: new Date(periodeListe[index].tom || dato)
							  }
					)
				)
					datoErGyldig = false
			})
		}

		return datoErGyldig
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
					orgnummer: requiredString,
					arbeidsforholdId: requiredNumber.transform(num => (isNaN(num) ? undefined : num))
				})
			),
			detaljertSykemelding: ifPresent(
				'$sykemelding.detaljertSykemelding',
				Yup.object({
					startDato: requiredDate,
					hovedDiagnose: Yup.object({
						diagnose: requiredString,
						diagnosekode: requiredString,
						system: requiredString
					}),
					biDiagnoser: Yup.array().of(
						ifPresent(
							'$sykemelding.detaljertSykemelding.biDiagnoser[0].diagnose',
							Yup.object({
								diagnose: requiredString,
								diagnosekode: requiredString,
								system: requiredString
							})
						)
					),
					// Vis denne når lege-oppslag er klart
					// lege: Yup.object({
					// 	etternavn: requiredString,
					// 	fornavn: requiredString,
					// 	ident: requiredString,
					// 	hprId: requiredString
					// }),
					arbeidsgiver: Yup.object({
						navn: requiredString,
						stillingsprosent: requiredNumber.transform(num => (isNaN(num) ? undefined : num)),
						yrkesbetegnelse: requiredString
					}),
					perioder: Yup.array().of(
						Yup.object({
							aktivitet: Yup.object({
								// TRENGER KANSKJE IKKE DENNE?
								// aktivitet: Yup.string().nullable(),
								// behandlingsdager: Yup.number().transform(num => (isNaN(num) ? undefined : num)),
								// grad: Yup.number().transform(num => (isNaN(num) ? undefined : num)),
								// reisetilskudd: Yup.boolean()
							}),
							fom: overlappendePerioderTest(requiredDate, false),
							tom: overlappendePerioderTest(requiredDate, true)
						})
					)
				})
			)
		})
	)
}

import * as Yup from 'yup'
import { requiredString, requiredDate, ifPresent } from '~/utils/YupValidations'

const partnere = Yup.array().of(
	Yup.object({
		identtype: Yup.string(),
		kjonn: Yup.string().nullable(),
		sivilstander: Yup.array().of(
			Yup.object({ sivilstand: requiredString, sivilstandRegdato: requiredDate })
		), // TODO sjekk at nyeste kommer øverst
		harFellesAdresse: Yup.boolean()
	})
)

const barn = Yup.array().of(
	Yup.object({
		identtype: Yup.string(),
		kjonn: Yup.string().nullable(),
		barnType: requiredString,
		partnerNr: Yup.number()
			// .test('range', 'feil!', function erGyldigPartner(val) {
			// 	const values = this.options.context
			// 	const antallPartnere = values.tpsf.relasjoner.partnere.length
			// 	if (
			// 		(!val &&
			// 			(values.tpsf.relasjoner.barn[0].barnType === 'MITT' ||
			// 				values.tpsf.relasjoner.barn[0].barnType === 'FELLES')) ||
			// 		(val > 0 && val <= antallPartnere)
			// 	)
			// 		return true
			// 	else return false
			// })
			.nullable(), // TODO fiks denne!
		borHos: requiredString,
		erAdoptert: Yup.boolean()
	})
)

export const validation = {
	tpsf: ifPresent(
		'$tpsf',
		Yup.object({
			foedtEtter: Yup.date().nullable(),
			foedtFoer: Yup.date().nullable(),
			doedsdato: Yup.date().nullable(),
			statsborgerskap: Yup.string(),
			statsborgerskapRegdato: Yup.date().nullable(),
			innvandretFraLand: Yup.string(),
			innvandretFraLandFlyttedato: Yup.date().nullable(),
			utvandretTilLand: Yup.string(),
			utvandretTilLandFlyttedato: Yup.date().nullable(),
			spesreg: Yup.string().when('utenFastBopel', {
				is: true,
				then: Yup.string().test(
					'is-not-kode6',
					'Kan ikke være "Kode 6" når "Uten fast bopel" er valgt.',
					value => value !== 'SPSF'
				)
			}),
			relasjoner: Yup.object({
				partnere: ifPresent('$tpsf.relasjoner.partnere', partnere),
				barn: ifPresent('$tpsf.relasjoner.barn', barn)
			})
		})
	)
}

import * as Yup from 'yup'
import { requiredString, isPresent } from '~/utils/YupValidations'

export const validation = {
	tpsf: Yup.object({
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
		postadresse: Yup.array().of(
			Yup.object({
				postLinje3: Yup.string().when('postLand', {
					is: 'NOR',
					then: requiredString
				}),
				postLand: requiredString
			})
		)
	})
}

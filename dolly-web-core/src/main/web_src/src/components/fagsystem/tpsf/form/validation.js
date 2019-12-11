import * as Yup from 'yup'
import { requiredString, ifKeyHasValue, ifPresent } from '~/utils/YupValidations'

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
				postLand: requiredString,
				postLinje1: requiredString,
				postLinje3: Yup.string()
					.matches(/\d{4}$/)
					.when('PostLinje2', {
						is: Yup.string().matches(/\d{4}$/),
						then: Yup.string().notRequired()
					})
			})
		)
	})
}

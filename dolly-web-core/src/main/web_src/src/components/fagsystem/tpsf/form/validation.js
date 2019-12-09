import * as Yup from 'yup'
import { requiredString, ifKeyHasValue, ifPresent } from '~/utils/YupValidations'
import { isPromise } from 'formik'

console.log('test :')
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
				/*postLinje3: Yup.string().when('postLinje2', {
					is: ifPresent,
					then: Yup.string()
						.matches(/^\d{4}(\s|$)/, 'a')
						.nullable(), //how to empty the textbox
					otherwise: Yup.string().notRequired(),
					validateOnBlur: true
				}),
				postLinje2: Yup.string().when('postLinje1', {
					is: ifPresent,
					then: Yup.string()
						.matches(/^\d{4}(\s|$)/, 'b')
						.nullable(), //empty when 3 is empty
					otherwise: Yup.string().notRequired(),
					validateOnBlur: true
				}),*/
				postLinje1: Yup.string().when('postLand', {
					is: 'NOR',
					then: Yup.string()
						.matches(/^\d{4}(\s|$)/, 'c')
						.nullable(), //empty when 3 is empty
					otherwise: Yup.string().notRequired(),
					validateOnBlur: true
				})
			})
		)
	})
}

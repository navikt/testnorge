import * as Yup from 'yup'
import _get from 'lodash/get'
import _findIndex from 'lodash/findIndex'
import { isBefore } from 'date-fns'
import { requiredString, requiredNumber, requiredDate, ifPresent } from '~/utils/YupValidations'

const partnere = Yup.array().of(
	Yup.object({
		identtype: Yup.string(),
		kjonn: Yup.string().nullable(),
		sivilstander: Yup.array().of(
			Yup.object({
				sivilstand: requiredString,
				sivilstandRegdato: Yup.string()
					.test(
						'is-before-last',
						'Dato må være før tidligere forhold (nyeste forhold settes først)',
						function validDate(val) {
							const values = this.options.context
							const path = this.options.path
							const thisDate = _get(values, path)
							const partnerIndex = path.charAt(path.indexOf('[') + 1)
							const sivilstander = values.tpsf.relasjoner.partnere[partnerIndex].sivilstander
							const forholdIndex = _findIndex(sivilstander, ['sivilstandRegdato', thisDate])
							if (forholdIndex === 0) return true
							return isBefore(thisDate, sivilstander[forholdIndex - 1].sivilstandRegdato)
						}
					)
					.required('Feltet er påkrevd')
			})
		),
		harFellesAdresse: Yup.boolean()
	})
)

const barn = Yup.array().of(
	Yup.object({
		identtype: Yup.string(),
		kjonn: Yup.string().nullable(),
		barnType: requiredString,
		partnerNr: Yup.number().nullable(),
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

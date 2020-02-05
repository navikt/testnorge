import * as Yup from 'yup'
import _get from 'lodash/get'
import _findIndex from 'lodash/findIndex'
import { isBefore } from 'date-fns'
import { requiredString, ifPresent, ifKeyHasValue, messages } from '~/utils/YupValidations'

const boadresse = Yup.object({
	gateadresse: ifKeyHasValue(
		'$tpsf.boadresse.adressetype',
		['GATE'],
		ifKeyHasValue(
			'$tpsf.adresseNrInfo',
			[null],
			Yup.string().required(
				'Bruk adressevelgeren over for å hente gyldige adresser og velge et av forslagene'
			)
		)
	),
	gardsnr: Yup.string().when('adressetype', {
		is: 'MATR',
		then: Yup.string()
			.required(messages.required)
			.max(5, 'Gårdsnummeret må være under 99999')
	}),
	bruksnr: Yup.string().when('adressetype', {
		is: 'MATR',
		then: Yup.string()
			.required(messages.required)
			.max(4, 'Bruksnummeret må være under 9999')
	}),
	festnr: Yup.string().max(4, 'Festenummer må være under 9999'),
	undernr: Yup.string().max(3, 'Undernummer må være under 999'),
	postnr: Yup.string().when('adressetype', { is: 'MATR', then: requiredString }),
	kommunenr: Yup.string().when('adressetype', { is: 'MATR', then: requiredString })
})

const adresseNrInfo = Yup.object({
	nummer: Yup.string().when('nummertype', {
		is: v => v,
		then: requiredString
	})
}).nullable()

export const sivilstander = Yup.array().of(
	Yup.object({
		sivilstand: Yup.string()
			.test('is-not-ugift', 'Ugyldig sivilstand for partner', value => value !== 'UGIF')
			.required(messages.required),
		sivilstandRegdato: Yup.string()
			.test(
				'is-before-last',
				'Dato må være før tidligere forhold (nyeste forhold settes først)',
				function validDate(val) {
					const values = this.options.context
					const path = this.options.path
					const thisDate = _get(values, path)
					const partnerIndex = path.charAt(path.indexOf('[') + 1)
					const sivilstander =
						//Linja med partnerE kan fjernes når Dolly2.0 prodsettes. Endres backend
						_get(values.tpsf.relasjoner.partnere, `${[partnerIndex]}.sivilstander`) ||
						_get(values.tpsf.relasjoner.partner, `${[partnerIndex]}.sivilstander`)
					const forholdIndex = _findIndex(sivilstander, ['sivilstandRegdato', thisDate])
					if (forholdIndex === 0) return true
					return isBefore(thisDate, sivilstander[forholdIndex - 1].sivilstandRegdato)
				}
			)
			.nullable()
			.required(messages.required)
	})
)

const partnere = Yup.array()
	.of(
		Yup.object({
			identtype: Yup.string(),
			kjonn: Yup.string().nullable(),
			alder: Yup.number()
				.transform(num => (isNaN(num) ? undefined : num))
				.min(1, 'Alder må være høyere enn 0')
				.max(99, 'Alder må være lavere enn 100'),
			foedtEtter: Yup.date().nullable(),
			foedtFoer: Yup.date().nullable(),
			spesreg: Yup.string()
				.when('utenFastBopel', {
					is: true,
					then: Yup.string().test(
						'is-not-kode6',
						'Kan ikke være "Kode 6" når "Uten fast bopel" er valgt.',
						value => value !== 'SPSF'
					)
				})
				.nullable(),
			utenFastBopel: Yup.boolean(),
			boadresse: Yup.object({
				kommunenr: Yup.string().nullable()
			}),
			sivilstander: sivilstander,
			harFellesAdresse: Yup.boolean()
		})
	)
	.nullable()

const barn = Yup.array()
	.of(
		Yup.object({
			identtype: Yup.string(),
			kjonn: Yup.string().nullable(),
			barnType: requiredString,
			partnerNr: Yup.number().nullable(),
			borHos: requiredString,
			erAdoptert: Yup.boolean(),
			alder: Yup.number()
				.transform(num => (isNaN(num) ? undefined : num))
				.min(1, 'Alder må være høyere enn 0')
				.max(99, 'Alder må være lavere enn 100'),
			spesreg: Yup.string()
				.when('utenFastBopel', {
					is: true,
					then: Yup.string().test(
						'is-not-kode6',
						'Kan ikke være "Kode 6" når "Uten fast bopel" er valgt.',
						value => value !== 'SPSF'
					)
				})
				.nullable(),
			utenFastBopel: Yup.boolean(),
			boadresse: Yup.object({
				kommunenr: Yup.string().nullable()
			})
		})
	)
	.nullable()

export const validation = {
	tpsf: ifPresent(
		'$tpsf',
		Yup.object({
			alder: Yup.number()
				.min(1, 'Alder må være høyere enn 0')
				.max(99, 'Alder må være lavere enn 100')
				.typeError(messages.required),
			foedtEtter: Yup.date().nullable(),
			foedtFoer: Yup.date().nullable(),
			doedsdato: Yup.date().nullable(),
			kjonn: ifPresent('$tpsf.kjonn', requiredString),
			statsborgerskap: ifPresent('$tpsf.statsborgerskap', requiredString),
			statsborgerskapRegdato: Yup.date().nullable(),
			innvandretFraLand: ifPresent('$tpsf.innvandretFraLand', requiredString),
			innvandretFraLandFlyttedato: Yup.date().nullable(),
			utvandretTilLand: ifPresent('$tpsf.utvandretTilLand', requiredString),
			utvandretTilLandFlyttedato: Yup.date().nullable(),
			sprakKode: ifPresent('$tpsf.sprakKode', requiredString),
			spesreg: ifPresent(
				'$tpsf.spesreg',
				Yup.string()
					.when('utenFastBopel', {
						is: true,
						then: Yup.string().test(
							'is-not-kode6',
							'Kan ikke være "Kode 6" når "Uten fast bopel" er valgt.',
							value => value !== 'SPSF'
						)
					})
					.required(messages.required)
			),
			boadresse: ifPresent('$tpsf.boadresse', boadresse),
			adresseNrInfo: ifPresent('$tpsf.adresseNrInfo', adresseNrInfo),
			postadresse: Yup.array().of(
				Yup.object({
					postLinje3: Yup.string().when('postLand', {
						is: 'NOR',
						then: requiredString
					}),
					postLand: requiredString
				})
			),
			relasjoner: Yup.object({
				partnere: ifPresent('$tpsf.relasjoner.partnere', partnere),
				barn: ifPresent('$tpsf.relasjoner.barn', barn)
			})
		})
	)
}

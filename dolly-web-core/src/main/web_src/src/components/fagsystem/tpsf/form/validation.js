import * as Yup from 'yup'
import _get from 'lodash/get'
import { isAfter, addDays } from 'date-fns'
import Dataformatter from '~/utils/DataFormatter'
import {
	requiredString,
	requiredNumber,
	ifPresent,
	ifKeyHasValue,
	messages
} from '~/utils/YupValidations'
import { Tilleggsadresse } from './adresser/partials/Tilleggsadresse/Tilleggsadresse'

const boadresse = Yup.object({
	gateadresse: ifKeyHasValue(
		'$tpsf.boadresse.adressetype',
		['GATE'],
		ifKeyHasValue(
			'$tpsf.adresseNrInfo',
			[null],
			ifKeyHasValue(
				'$tpsf.utenFastBopel',
				[undefined, false],
				Yup.string().required(
					'Bruk adressevelgeren over for å hente gyldige adresser og velge et av forslagene'
				)
			)
		)
	),
	adressetype: requiredString,
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
	kommunenr: Yup.string()
		.when('adressetype', { is: 'MATR', then: requiredString })
		.nullable(),
	tilleggsadresse: Yup.object({
		tilleggType: ifPresent('$tpsf.boadresse.tilleggsadresse', requiredString),
		nummer: ifKeyHasValue(
			'$tpsf.boadresse.tilleggsadresse.tilleggType',
			['LEILIGHET_NR', 'SEKSJON_NR', 'BOLIG_NR'],
			requiredNumber.transform(num => (isNaN(num) ? undefined : num))
		)
	})
})

const adresseNrInfo = Yup.object({
	nummer: Yup.string().when('nummertype', {
		is: v => v,
		then: requiredString
	})
}).nullable()

export const sivilstander = Yup.array().of(
	Yup.object({
		sivilstand: Yup.string().required(messages.required),
		sivilstandRegdato: Yup.string()
			.test(
				'is-after-last',
				'Dato må være etter tidligere forhold (eldste forhold settes først)',
				function validDate(dato) {
					if (!dato) return true
					const values = this.options.context
					const path = this.options.path

					// Finn index av current partner og sivilstand
					// Ex path: tpsf.relasjoner.partnere[0].sivilstander[0].sivilstandRegdato
					const partnerIdx = parseInt(path.match(/partnere\[(.*?)\].sivilstander/i)[1])
					const sivilstandIdx = parseInt(path.match(/sivilstander\[(.*?)\].sivilstandRegdato/i)[1])

					// Da vi skal validere mot "forrige forhold" trenger vi ikke sjekke første
					if (partnerIdx === 0 && sivilstandIdx === 0) return true

					const getSivilstandRegdato = (pIdx, sIdx) =>
						_get(
							values.tpsf.relasjoner.partnere,
							`[${pIdx}].sivilstander[${sIdx}].sivilstandRegdato`
						)

					let prevDato
					if (sivilstandIdx > 0) {
						prevDato = getSivilstandRegdato(partnerIdx, sivilstandIdx - 1)
					} else {
						const prevPartnerSivilstandArr = _get(
							values.tpsf.relasjoner.partnere,
							`[${partnerIdx - 1}].sivilstander`
						)
						prevDato = getSivilstandRegdato(partnerIdx - 1, prevPartnerSivilstandArr.length - 1)
					}

					// Selve testen
					const dateValid = isAfter(new Date(dato), addDays(new Date(prevDato), 2))
					return (
						dateValid ||
						this.createError({
							message: `Dato må være etter tidligere forhold (${Dataformatter.formatDate(
								prevDato
							)}) og det må minst være 2 dager i mellom`,
							path: path
						})
					)
				}
			)
			.required(messages.required)
	})
)

const innvandringUtvandringDatoTest = schema => {
	return schema.test(
		'datoEtterSisteInnUtvandring',
		`Datoen må være etter siste inn-/utvandring (${''})`,
		function erEtterSisteDato(dato) {
			const values = this.options.context
			const personFoerLeggTil = values.personFoerLeggTil
			if (!dato || !personFoerLeggTil) return true

			const sisteDato = _get(personFoerLeggTil, 'tpsf.innvandretUtvandret[0].flyttedato')
			const dateValid = isAfter(new Date(dato), new Date(sisteDato))

			return (
				dateValid ||
				this.createError({
					message: `Dato må være etter siste inn-/utvandring (${Dataformatter.formatDate(
						sisteDato
					)})`,
					path: this.options.path
				})
			)
		}
	)
}

const foedtFoerOgEtterTest = (validation, validerFoedtFoer) => {
	const errorMsgFoedtFoer =
		'Født Før dato kan ikke være før Født Etter dato og det må være minst en dag mellom datoene.'
	const errorMsgFoedtEtter =
		'Født Etter dato kan ikke være etter Født Før dato og det må være minst en dag mellom datoene.'
	return validation.test(
		'range',
		validerFoedtFoer ? errorMsgFoedtFoer : errorMsgFoedtEtter,
		function isWithinTest(val) {
			if (!val) return true

			const values = this.options.context
			const path = this.path.substring(0, this.path.lastIndexOf('.'))

			const selectedDato = new Date(new Date(val).toDateString())
			const foedtEtterValue = _get(values, `${path}.foedtEtter`)
			const foedtFoerValue = _get(values, `${path}.foedtFoer`)

			if (validerFoedtFoer) {
				if (foedtEtterValue !== '' && foedtEtterValue !== undefined) {
					const foedtEtterDato = new Date(foedtEtterValue)
					foedtEtterDato.setDate(foedtEtterDato.getDate() + 1)
					if (selectedDato <= new Date(foedtEtterDato.toDateString())) return false
				}
			} else {
				if (foedtFoerValue !== '' && foedtFoerValue !== undefined) {
					const foedtFoerDato = new Date(foedtFoerValue)
					foedtFoerDato.setDate(foedtFoerDato.getDate() - 1)
					if (selectedDato >= new Date(foedtFoerDato.toDateString())) return false
				}
			}
			return true
		}
	)
}

const partnere = Yup.array()
	.of(
		Yup.object({
			identtype: Yup.string(),
			kjonn: Yup.string().nullable(),
			alder: Yup.number()
				.transform(num => (isNaN(num) ? undefined : num))
				.min(0, 'Alder må være et positivt tall')
				.max(119, 'Alder må være under 120'),
			foedtEtter: foedtFoerOgEtterTest(Yup.date().nullable(), false),
			foedtFoer: foedtFoerOgEtterTest(Yup.date().nullable(), true),
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
			sivilstander: ifPresent('$tpsf.relasjoner.partnere[0].sivilstander', sivilstander),
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
				.min(0, 'Alder må være et positivt tall')
				.max(119, 'Alder må være under 120'),
			foedtEtter: foedtFoerOgEtterTest(Yup.date().nullable(), false),
			foedtFoer: foedtFoerOgEtterTest(Yup.date().nullable(), true),
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

const testTelefonnummer = nr =>
	Yup.string()
		.max(20, 'Telefonnummer kan ikke ha mer enn 20 sifre')
		.when(`telefonLandskode_${nr}`, {
			is: '+47',
			then: Yup.string().length(8, 'Norsk telefonnummer må ha 8 sifre')
		})
		.required(messages.required)
		.matches(/^[1-9][0-9]*$/, 'Telefonnummer må være numerisk, og kan ikke starte med 0')

export const validation = {
	tpsf: ifPresent(
		'$tpsf',
		Yup.object({
			alder: Yup.number()
				.min(0, 'Alder må være et positivt tall')
				.max(119, 'Alder må være under 120')
				.typeError(messages.required),
			foedtEtter: foedtFoerOgEtterTest(Yup.date().nullable(), false),
			foedtFoer: foedtFoerOgEtterTest(Yup.date().nullable(), true),
			doedsdato: Yup.date().nullable(),
			kjonn: ifPresent('$tpsf.kjonn', requiredString),
			statsborgerskap: ifPresent('$tpsf.statsborgerskap', requiredString),
			statsborgerskapRegdato: Yup.date().nullable(),
			innvandretFraLand: ifPresent('$tpsf.innvandretFraLand', requiredString),
			innvandretFraLandFlyttedato: ifPresent(
				'$tpsf.innvandretFraLandFlyttedato',
				innvandringUtvandringDatoTest(Yup.date().nullable())
			),
			utvandretTilLand: ifPresent('$tpsf.utvandretTilLand', requiredString),
			utvandretTilLandFlyttedato: ifPresent(
				'$tpsf.utvandretTilLandFlyttedato',
				innvandringUtvandringDatoTest(Yup.date().nullable())
			),
			sprakKode: ifPresent('$tpsf.sprakKode', requiredString),
			telefonLandskode_1: ifPresent('$tpsf.telefonLandskode_1', requiredString),
			telefonnummer_1: ifPresent('$tpsf.telefonnummer_1', testTelefonnummer('1')),
			telefonLandskode_2: ifPresent('$tpsf.telefonLandskode_2', requiredString).nullable(),
			telefonnummer_2: ifPresent('$tpsf.telefonnummer_2', testTelefonnummer('2')).nullable(),
			spesreg: ifPresent(
				'$tpsf.spesreg',
				Yup.string().when('utenFastBopel', {
					is: true,
					then: Yup.string().test(
						'is-not-kode6',
						'Kan ikke være "Kode 6" når "Uten fast bopel" er valgt.',
						value => value !== 'SPSF'
					)
				})
			).nullable(),
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

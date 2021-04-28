import * as Yup from 'yup'
import _get from 'lodash/get'
import { addDays, isAfter, isBefore } from 'date-fns'
import Dataformatter from '~/utils/DataFormatter'
import {
	ifKeyHasValue,
	ifPresent,
	messages,
	requiredDate,
	requiredNumber,
	requiredString
} from '~/utils/YupValidations'

const boadresse = Yup.object({
	gateadresse: ifKeyHasValue(
		'$tpsf.boadresse.adressetype',
		['GATE'],
		ifKeyHasValue(
			'$tpsf.adresseNrInfo',
			[null],
			ifKeyHasValue(
				'$tpsf.boadresse.tilleggsadresse',
				[undefined],
				ifKeyHasValue(
					'$tpsf.utenFastBopel',
					[undefined, false],
					Yup.string().required(
						'Bruk adressevelgeren over for å hente gyldige adresser og velge et av forslagene'
					)
				)
			)
		)
	),
	bolignr: Yup.string()
		.matches(
			/^[HULK]\d{4}$/,
			'Bruksenhetsnummer består av bokstaven H, L, U eller K etterfulgt av 4 sifre'
		)
		.transform((i, j) => (j === '' ? null : i))
		.nullable(),
	adressetype: requiredString,
	gardsnr: Yup.mixed().when('adressetype', {
		is: 'MATR',
		then: ifKeyHasValue(
			'$tpsf.boadresse.tilleggsadresse',
			[undefined],
			Yup.string()
				.required(messages.required)
				.max(5, 'Gårdsnummeret må være under 99999')
		)
	}),
	bruksnr: Yup.mixed().when('adressetype', {
		is: 'MATR',
		then: ifKeyHasValue(
			'$tpsf.boadresse.tilleggsadresse',
			[undefined],
			Yup.string()
				.required(messages.required)
				.max(4, 'Bruksnummeret må være under 9999')
		)
	}),
	festnr: Yup.string().max(4, 'Festenummer må være under 9999'),
	undernr: Yup.string().max(3, 'Undernummer må være under 999'),
	postnr: Yup.mixed().when('adressetype', {
		is: 'MATR',
		then: ifKeyHasValue('$tpsf.boadresse.tilleggsadresse', [undefined], requiredString)
	}),
	kommunenr: Yup.mixed()
		.when('adressetype', {
			is: 'MATR',
			then: ifKeyHasValue('$tpsf.boadresse.tilleggsadresse', [undefined], requiredString)
		})
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
	nummer: Yup.mixed().when('nummertype', {
		is: v => v,
		then: ifKeyHasValue('$tpsf.boadresse.tilleggsadresse', [undefined], requiredString)
	})
}).nullable()

const midlertidigAdresse = Yup.object({
	adressetype: requiredString,
	gyldigTom: Yup.string().test(
		'is-within-one-year-from-today',
		'Dato må være innen ett år fra i dag',
		function validDate(dato) {
			if (!dato) return true
			const ettAarFraIDag = new Date(new Date().setFullYear(new Date().getFullYear() + 1))
			const dateValid =
				isAfter(new Date(dato), addDays(new Date(), -1)) && isBefore(new Date(dato), ettAarFraIDag)
			return dateValid
		}
	),
	gateadresseNrInfo: Yup.object({
		nummertype: ifPresent('$tpsf.midlertidigAdresse.gateadresseNrInfo', requiredString),
		nummer: ifPresent('$tpsf.midlertidigAdresse.gateadresseNrInfo', requiredString)
	}),
	norskAdresse: Yup.object({
		tilleggsadresse: Yup.object({
			tilleggType: ifPresent(
				'$tpsf.midlertidigAdresse.norskAdresse.tilleggsadresse.tilleggType',
				requiredString
			),
			nummer: ifKeyHasValue(
				'$tpsf.midlertidigAdresse.norskAdresse.tilleggsadresse.tilleggType',
				'BOLIG_NR',
				requiredString
			)
		}),
		postnr: ifPresent('$tpsf.midlertidigAdresse.norskAdresse.postnr', requiredString),
		gatenavn: ifKeyHasValue(
			'$tpsf.midlertidigAdresse.adressetype',
			['GATE'],
			ifPresent(
				'$tpsf.midlertidigAdresse.norskAdresse.gatenavn',
				Yup.string().required(
					'Bruk adressevelgeren over for å hente gyldige adresser og velge et av forslagene'
				)
			)
		),
		gatekode: Yup.string(),
		husnr: Yup.string(),
		eiendomsnavn: ifKeyHasValue('$tpsf.midlertidigAdresse.adressetype', 'STED', requiredString),
		postboksnr: Yup.string()
			.min(1, 'Feltet er påkrevd')
			.max(5, 'Nummer kan ha maks fem siffer'),
		postboksAnlegg: ifKeyHasValue('$tpsf.midlertidigAdresse.adressetype', 'PBOX', requiredString)
	}),
	utenlandskAdresse: Yup.object({
		postLinje1: ifPresent('$tpsf.midlertidigAdresse.utenlandskAdresse', requiredString),
		postLinje2: Yup.string(),
		postLinje3: Yup.string(),
		postLand: ifPresent('$tpsf.midlertidigAdresse.utenlandskAdresse', requiredString)
	})
})

export const sivilstander = Yup.array().of(
	Yup.object({
		sivilstand: Yup.string().required(messages.required),
		sivilstandRegdato: Yup.date()
			.transform((i, j) => (j === null || j === '' ? undefined : i))
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

					const getSisteForholdDato = () => {
						if (!values.personFoerLeggTil?.tpsf?.sivilstander) {
							return null
						}
						const datoer = values.personFoerLeggTil.tpsf.sivilstander
							.filter(sivstand => sivstand.sivilstandRegdato)
							.map(sivilstand => new Date(sivilstand.sivilstandRegdato))
							.sort((a, b) => b.getTime() - a.getTime()) // Seneste dato på første index
						return datoer && datoer.length > 0 && datoer[0].toISOString().substring(0, 10)
					}

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
					if (!prevDato) {
						prevDato = getSisteForholdDato()
					}

					// Selve testen
					const dateValidAfter = isAfter(new Date(dato), addDays(new Date(prevDato), 2))
					const dateValidBeforeToday = isBefore(new Date(dato), Date.now())
					return (
						(dateValidAfter && dateValidBeforeToday) ||
						this.createError({
							message: !dateValidAfter
								? `Dato må være etter tidligere forhold (${Dataformatter.formatDate(
										prevDato
								  )}), og det må minst være 2 dager i mellom`
								: 'Dato kan ikke være etter dagens dato',
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
		'Født Før dato kan ikke være før Født Etter dato eller etter dagens dato'
	const errorMsgFoedtEtter =
		'Født Etter dato kan ikke være etter Født Før dato eller etter dagens dato.'

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

			const foedtEtterDato = new Date(foedtEtterValue)
			const foedtFoerDato = new Date(foedtFoerValue)

			const identtype = _get(values, `${path}.identtype`)
			if (identtype === 'FDAT') {
				return true
			}

			if (
				(!validerFoedtFoer && foedtEtterDato > Date.now()) ||
				(validerFoedtFoer && foedtFoerDato > Date.now())
			)
				return false

			if (foedtEtterDato)
				if (validerFoedtFoer) {
					if (foedtEtterValue !== '' && foedtEtterValue !== undefined) {
						foedtEtterDato.setDate(foedtEtterDato.getDate())
						if (selectedDato < new Date(foedtEtterDato.toDateString())) return false
					}
				} else {
					if (foedtFoerValue !== '' && foedtFoerValue !== undefined) {
						foedtFoerDato.setDate(foedtFoerDato.getDate())
						if (selectedDato > new Date(foedtFoerDato.toDateString())) return false
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

const requiredHvisIkkeDoed = path =>
	ifPresent(
		path,
		Yup.mixed().when('doedsdato', {
			is: val => val === undefined || val === null,
			then: requiredString
		})
	)

const requiredHvisDoedfoedt = path =>
	ifPresent(
		path,
		Yup.mixed().when('identtype', {
			is: val => val === 'FDAT',
			then: requiredDate
		})
	)

const barn = Yup.array()
	.of(
		Yup.object({
			identtype: Yup.string(),
			kjonn: Yup.string().nullable(),
			barnType: ifPresent('$tpsf.relasjoner.barn[0].barnType', requiredString),
			partnerNr: Yup.number().nullable(),
			borHos: requiredHvisIkkeDoed('$tpsf.relasjoner.barn[0].borHos'),
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
			}),
			foedselsdato: requiredHvisDoedfoedt('$tpsf.relasjoner.barn[0].foedselsdato'),
			doedsdato: Yup.date().nullable()
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
			statsborgerskapTildato: Yup.date().nullable(),
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
			egenAnsattDatoFom: ifPresent(
				'$tpsf.egenAnsattDatoFom',
				Yup.string().test(
					'is-before-today',
					'Dato kan ikke være etter dagens dato',
					function validDate(dato) {
						return isBefore(new Date(dato), new Date())
					}
				)
			),
			egenAnsattDatoTom: ifPresent(
				'$tpsf.egenAnsattDatoTom',
				Yup.string().test('is-after-dato-fom', 'Dato må være etter fra-dato', function validDate(
					dato
				) {
					const values = this.options.context
					return isAfter(new Date(dato), new Date(_get(values, 'tpsf.egenAnsattDatoFom')))
				})
			),
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
			vergemaal: ifPresent(
				'$tpsf.vergemaal',
				Yup.object({
					embete: requiredString.nullable(),
					sakType: requiredString.nullable(),
					vedtakDato: requiredDate
				})
			),
			fullmakt: ifPresent(
				'$tpsf.fullmakt',
				Yup.object({
					kilde: requiredString,
					omraader: Yup.array()
						.of(Yup.string())
						.required('Velg minst ett område'),
					gyldigFom: requiredDate,
					gyldigTom: requiredDate
				})
			),
			boadresse: ifPresent('$tpsf.boadresse', boadresse),
			adresseNrInfo: ifPresent('$tpsf.adresseNrInfo', adresseNrInfo),
			midlertidigAdresse: ifPresent('$tpsf.midlertidigAdresse', midlertidigAdresse),
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
			}),
			identtype: ifPresent('$tpsf.identtype', requiredString)
		})
	)
}

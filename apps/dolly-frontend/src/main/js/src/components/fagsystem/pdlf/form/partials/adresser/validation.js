import * as Yup from 'yup'
import { requiredString } from '~/utils/YupValidations'
import { testDatoFom, testDatoTom } from '~/components/fagsystem/utils'

const gyldigDatoFom = Yup.lazy((val) =>
	val instanceof Date
		? testDatoFom(Yup.date().nullable(), 'gyldigTilOgMed')
		: testDatoFom(Yup.string().nullable(), 'gyldigTilOgMed')
)

const gyldigDatoTom = Yup.lazy((val) =>
	val instanceof Date
		? testDatoTom(Yup.date().nullable(), 'gyldigFraOgMed')
		: testDatoTom(Yup.string().nullable(), 'gyldigFraOgMed')
)

export const vegadresse = Yup.object({
	adressekode: Yup.string().nullable(),
	adressenavn: Yup.string().nullable(),
	tilleggsnavn: Yup.string().nullable(),
	bruksenhetsnummer: Yup.string().nullable(),
	husbokstav: Yup.string().nullable(),
	husnummer: Yup.lazy((val) =>
		typeof val === 'string' ? Yup.string().nullable() : Yup.number().nullable()
	),
	kommunenummer: Yup.string().nullable(),
	postnummer: Yup.string().nullable(),
})

export const matrikkeladresse = Yup.object({
	kommunenummer: Yup.string().nullable(),
	gaardsnummer: Yup.lazy((val) =>
		typeof val === 'string'
			? Yup.string().max(5, 'Gårdsnummeret må være under 99999').nullable()
			: Yup.number().max(99999, 'Gårdsnummeret må være under 99999').nullable()
	),
	bruksnummer: Yup.lazy((val) =>
		typeof val === 'string'
			? Yup.string().max(4, 'Bruksnummeret må være under 9999').nullable()
			: Yup.number().max(9999, 'Bruksnummeret må være under 9999').nullable()
	),
	postnummer: Yup.string().nullable(),
	bruksenhetsnummer: Yup.string()
		.matches(
			/^[HULK]\d{4}$/,
			'Bruksenhetsnummer består av bokstaven H, L, U eller K etterfulgt av 4 sifre'
		)
		.transform((i, j) => (j === '' ? null : i))
		.nullable(),
	tilleggsnavn: Yup.string().nullable(),
})

const utenlandskAdresse = Yup.object({
	adressenavnNummer: Yup.string().nullable(),
	postboksNummerNavn: Yup.string().nullable(),
	postkode: Yup.string().nullable(),
	bySted: Yup.string().nullable(),
	landkode: Yup.string().nullable(),
	bygningEtasjeLeilighet: Yup.string().nullable(),
	regionDistriktOmraade: Yup.string().nullable(),
})

export const postboksadresse = Yup.object({
	postboks: requiredString.nullable(),
	postbokseier: Yup.string().nullable(),
	postnummer: requiredString.nullable(),
})

const ukjentBosted = Yup.object({
	bostedskommune: Yup.string().nullable(),
})

export const bostedsadresse = Yup.object({
	adressetype: Yup.string().nullable(),
	angittFlyttedato: Yup.lazy((val) =>
		val instanceof Date ? Yup.date().nullable() : Yup.string().nullable()
	),
	gyldigFraOgMed: gyldigDatoFom,
	gyldigTilOgMed: gyldigDatoTom,
	vegadresse: Yup.mixed().when('adressetype', {
		is: 'VEGADRESSE',
		then: vegadresse,
	}),
	matrikkeladresse: Yup.mixed().when('adressetype', {
		is: 'MATRIKKELADRESSE',
		then: matrikkeladresse,
	}),
	utenlandskAdresse: Yup.mixed().when('adressetype', {
		is: 'UTENLANDSK_ADRESSE',
		then: utenlandskAdresse,
	}),
	ukjentBosted: Yup.mixed().when('adressetype', {
		is: 'UKJENT_BOSTED',
		then: ukjentBosted,
	}),
})

export const oppholdsadresse = Yup.object({
	adressetype: Yup.string().nullable(),
	gyldigFraOgMed: gyldigDatoFom,
	gyldigTilOgMed: gyldigDatoTom,
	vegadresse: Yup.mixed().when('adressetype', {
		is: 'VEGADRESSE',
		then: vegadresse,
	}),
	matrikkeladresse: Yup.mixed().when('adressetype', {
		is: 'MATRIKKELADRESSE',
		then: matrikkeladresse,
	}),
	utenlandskAdresse: Yup.mixed().when('adressetype', {
		is: 'UTENLANDSK_ADRESSE',
		then: utenlandskAdresse,
	}),
	oppholdAnnetSted: Yup.mixed().when('adressetype', {
		is: 'OPPHOLD_ANNET_STED',
		then: requiredString.nullable(),
	}),
})

export const kontaktadresse = Yup.object({
	adressetype: Yup.string().nullable(),
	gyldigFraOgMed: testDatoFom(Yup.string().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.string().nullable(), 'gyldigFraOgMed'),
	vegadresse: Yup.mixed().when('adressetype', {
		is: 'VEGADRESSE',
		then: vegadresse,
	}),
	utenlandskAdresse: Yup.mixed().when('adressetype', {
		is: 'UTENLANDSK_ADRESSE',
		then: utenlandskAdresse,
	}),
	postboksadresse: Yup.mixed().when('adressetype', {
		is: 'POSTBOKSADRESSE',
		then: postboksadresse,
	}),
})

export const adressebeskyttelse = Yup.object({
	gradering: requiredString.nullable(),
})

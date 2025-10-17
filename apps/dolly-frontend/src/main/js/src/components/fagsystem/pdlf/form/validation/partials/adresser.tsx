import * as Yup from 'yup'
import { requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import { isAfter, isBefore } from 'date-fns'

const gyldigDatoFom = Yup.lazy((val) =>
	val instanceof Date
		? testDatoFom(Yup.date().nullable(), 'gyldigTilOgMed')
		: testDatoFom(Yup.string().nullable(), 'gyldigTilOgMed'),
)

const gyldigDatoTom = Yup.lazy((val) =>
	val instanceof Date
		? testDatoTom(Yup.date().nullable(), 'gyldigFraOgMed')
		: testDatoTom(Yup.string().nullable(), 'gyldigFraOgMed'),
)

const datoOverlapper = (
	nyDatoFra: string,
	gjeldendeDatoFra: string,
	gjeldendeDatoTil: string,
): boolean => {
	if (!gjeldendeDatoFra || !gjeldendeDatoTil) return false

	return (
		isBefore(new Date(nyDatoFra), new Date(gjeldendeDatoTil)) &&
		!isBefore(new Date(nyDatoFra), new Date(gjeldendeDatoFra))
	)
}

const overlapperMedAdresse = (
	originalFradato: string,
	originalTildato: string | null,
	adresseListe: any[],
	nyAdresse: boolean,
): boolean => {
	for (let adresse of adresseListe) {
		const fraDato = adresse.gyldigFraOgMed
		const tilDato = adresse.gyldigTilOgMed

		if (!fraDato) {
			continue
		}

		if (
			datoOverlapper(originalFradato, fraDato, tilDato) ||
			datoOverlapper(fraDato, originalFradato, originalTildato)
		) {
			return true
		}

		if (!tilDato) {
			if (nyAdresse && isAfter(new Date(originalFradato), new Date(fraDato))) {
				return true
			}
			if (
				!nyAdresse &&
				!originalTildato &&
				(new Date(originalFradato).getDate() === new Date(fraDato).getDate() ||
					!isAfter(new Date(originalFradato), new Date(fraDato)))
			) {
				return true
			}
		}
	}
	return false
}

const validFradato = () => {
	return Yup.string()
		.test(
			'gyldig-fra-dato',
			'Periode kan ikke overlappe med en annen bostedadresse',
			(val, testContext) => {
				if (!val) {
					return true
				}
				const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
				const personFoerLeggTil = testContext?.options?.context?.personFoerLeggTil

				const nyeAdresser = fullForm?.pdldata?.person?.bostedsadresse
					? [...fullForm.pdldata.person.bostedsadresse]
					: [fullForm.bostedsadresse]
				let tildato = null
				let adresseIndex: number | null = null
				for (let i = 0; i < nyeAdresser.length; i++) {
					if (nyeAdresser[i]?.gyldigFraOgMed + '' === val) {
						tildato = nyeAdresser[i].gyldigTilOgMed
							? new Date(nyeAdresser[i].gyldigTilOgMed).toISOString().substring(0, 19)
							: null
						adresseIndex = i
						break
					}
				}

				if (adresseIndex !== null) {
					nyeAdresser.splice(adresseIndex, 1)
				}
				const tidligereAdresser = personFoerLeggTil?.pdlforvalter?.person?.bostedsadresse || []

				return !(
					overlapperMedAdresse(val, tildato, nyeAdresser, true) ||
					overlapperMedAdresse(val, tildato, tidligereAdresser, false)
				)
			},
		)
		.nullable()
}

export const vegadresse = Yup.object({
	adressekode: Yup.string().nullable(),
	adressenavn: Yup.string().nullable(),
	tilleggsnavn: Yup.string().nullable(),
	bruksenhetsnummer: Yup.string().nullable(),
	husbokstav: Yup.string().nullable(),
	husnummer: Yup.lazy((val) =>
		typeof val === 'string' ? Yup.string().nullable() : Yup.number().nullable(),
	),
	kommunenummer: Yup.string().nullable(),
	postnummer: Yup.string().nullable(),
})

export const matrikkeladresse = Yup.object({
	kommunenummer: Yup.string().nullable(),
	gaardsnummer: Yup.lazy((val) =>
		typeof val === 'string'
			? Yup.string().max(5, 'Gårdsnummeret må være under 99999').nullable()
			: Yup.number().max(99999, 'Gårdsnummeret må være under 99999').nullable(),
	),
	bruksnummer: Yup.lazy((val) =>
		typeof val === 'string'
			? Yup.string().max(4, 'Bruksnummeret må være under 9999').nullable()
			: Yup.number().max(9999, 'Bruksnummeret må være under 9999').nullable(),
	),
	postnummer: Yup.string().nullable(),
	bruksenhetsnummer: Yup.string()
		.matches(
			/^[HULK]\d{4}$/,
			'Bruksenhetsnummer består av bokstaven H, L, U eller K etterfulgt av 4 sifre',
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

const postadresseIFrittFormat = Yup.object({
	adresselinjer: Yup.array().of(Yup.string().nullable()).nullable(),
	postnummer: Yup.string().nullable(),
})

const utenlandskAdresseIFrittFormat = Yup.object({
	adresselinjer: Yup.array().of(Yup.string().nullable()).nullable(),
	postkode: Yup.string().nullable(),
	byEllerStedsnavn: Yup.string().nullable(),
	landkode: Yup.string().nullable(),
})

export const postboksadresse = Yup.object({
	postboks: requiredString,
	postbokseier: Yup.string().nullable(),
	postnummer: requiredString,
})

const ukjentBosted = Yup.object({
	bostedskommune: Yup.string().nullable(),
})

export const bostedsadresse = Yup.object({
	adressetype: Yup.string().nullable(),
	angittFlyttedato: Yup.lazy((val) =>
		val instanceof Date ? Yup.date().nullable() : Yup.string().nullable(),
	),
	gyldigFraOgMed: validFradato(),
	gyldigTilOgMed: gyldigDatoTom,
	vegadresse: Yup.mixed().when('adressetype', {
		is: 'VEGADRESSE',
		then: () => vegadresse,
		otherwise: () => Yup.mixed().nullable(),
	}),
	matrikkeladresse: Yup.mixed().when('adressetype', {
		is: 'MATRIKKELADRESSE',
		then: () => matrikkeladresse,
		otherwise: () => Yup.mixed().nullable(),
	}),
	utenlandskAdresse: Yup.mixed().when('adressetype', {
		is: 'UTENLANDSK_ADRESSE',
		then: () => utenlandskAdresse,
		otherwise: () => Yup.mixed().nullable(),
	}),
	ukjentBosted: Yup.mixed().when('adressetype', {
		is: 'UKJENT_BOSTED',
		then: () => ukjentBosted,
		otherwise: () => Yup.mixed().nullable(),
	}),
})

export const oppholdsadresse = Yup.object({
	adressetype: Yup.string().nullable(),
	gyldigFraOgMed: gyldigDatoFom,
	gyldigTilOgMed: gyldigDatoTom,
	vegadresse: Yup.mixed().when('adressetype', {
		is: 'VEGADRESSE',
		then: () => vegadresse,
		otherwise: () => Yup.mixed().nullable(),
	}),
	matrikkeladresse: Yup.mixed().when('adressetype', {
		is: 'MATRIKKELADRESSE',
		then: () => matrikkeladresse,
		otherwise: () => Yup.mixed().nullable(),
	}),
	utenlandskAdresse: Yup.mixed().when('adressetype', {
		is: 'UTENLANDSK_ADRESSE',
		then: () => utenlandskAdresse,
		otherwise: () => Yup.mixed().nullable(),
	}),
	oppholdAnnetSted: Yup.mixed().when('adressetype', {
		is: 'OPPHOLD_ANNET_STED',
		then: () => requiredString,
		otherwise: () => Yup.mixed().nullable(),
	}),
})

export const kontaktadresse = Yup.object({
	adressetype: Yup.string().nullable(),
	gyldigFraOgMed: gyldigDatoFom,
	gyldigTilOgMed: gyldigDatoTom,
	vegadresse: Yup.mixed().when('adressetype', {
		is: 'VEGADRESSE',
		then: () => vegadresse,
		otherwise: () => Yup.mixed().nullable(),
	}),
	utenlandskAdresse: Yup.mixed().when('adressetype', {
		is: 'UTENLANDSK_ADRESSE',
		then: () => utenlandskAdresse,
		otherwise: () => Yup.mixed().nullable(),
	}),
	postboksadresse: Yup.mixed().when('adressetype', {
		is: 'POSTBOKSADRESSE',
		then: () => postboksadresse,
		otherwise: () => Yup.mixed().nullable(),
	}),
	postadresseIFrittFormat: Yup.mixed().when('adressetype', {
		is: 'POSTADRESSE_I_FRITT_FORMAT',
		then: () => postadresseIFrittFormat,
		otherwise: () => Yup.mixed().nullable(),
	}),
	utenlandskAdresseIFrittFormat: Yup.mixed().when('adressetype', {
		is: 'UTENLANDSK_ADRESSE_I_FRITT_FORMAT',
		then: () => utenlandskAdresseIFrittFormat,
		otherwise: () => Yup.mixed().nullable(),
	}),
})

export const adressebeskyttelse = Yup.object({
	gradering: requiredString,
})

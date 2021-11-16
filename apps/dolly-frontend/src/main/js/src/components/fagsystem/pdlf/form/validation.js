import * as Yup from 'yup'
import { ifKeyHasValue, ifPresent, requiredDate, requiredString } from '~/utils/YupValidations'

const personnavnSchema = Yup.object({
	fornavn: Yup.string(),
	mellomnavn: Yup.string(),
	etternavn: Yup.string(),
})

const nyPerson = Yup.object({
	identtype: Yup.string().nullable(),
	kjoenn: Yup.string().nullable(),
	foedtEtter: Yup.string().nullable(),
	foedtFoer: Yup.string().nullable(),
	alder: Yup.string().nullable(),
	syntetisk: Yup.boolean(),
	nyttNavn: Yup.object({
		hasMellomnavn: Yup.boolean(),
	}),
	statsborgerskapLandkode: Yup.string().nullable(),
	gradering: Yup.string().nullable(),
})

const bostedsadresse = Yup.array().of(
	Yup.object({
		utenlandskAdresse: Yup.object({
			adressenavnNummer: Yup.string().nullable(),
			postboksNummerNavn: Yup.string().nullable(),
			postkode: Yup.string().nullable(),
			bySted: Yup.string().nullable(),
			landkode: Yup.string().nullable(),
			bygningEtasjeLeilighet: Yup.string().nullable(),
			regionDistriktOmraade: Yup.string().nullable(),
		}),
	})
)

const fullmakt = Yup.array().of(
	Yup.object({
		omraader: Yup.array().min(1, 'Velg minst ett område'),
		gyldigFraOgMed: requiredDate,
		gyldigTilOgMed: requiredDate,
		nyFullmektig: nyPerson,
	})
)

const falskIdentitet = Yup.array().of(
	Yup.object({
		rettIdentErUkjent: Yup.boolean(),
		rettIdentitetVedIdentifikasjonsnummer: Yup.string().nullable(),
		rettIdentitetVedOpplysninger: Yup.object({
			foedselsdato: Yup.string().nullable(),
			kjoenn: Yup.string().nullable(),
			personnavn: personnavnSchema.nullable(),
			statsborgerskap: Yup.array().of(Yup.string()),
		}),
	})
)

const utenlandskId = Yup.array().of(
	Yup.object({
		identifikasjonsnummer: requiredString,
		opphoert: requiredString,
		utstederland: requiredString,
	})
)

const kontaktDoedsbo = Yup.array().of(
	Yup.object({
		skifteform: requiredString.nullable(),
		attestutstedelsesdato: requiredDate,
		adresse: Yup.object({
			adresselinje1: requiredString,
			adresselinje2: Yup.string().nullable(),
			postnummer: requiredString,
			poststedsnavn: requiredString,
			landkode: requiredString,
		}),
		advokatSomKontakt: ifPresent(
			'$pdldata.person.kontaktinformasjonForDoedsbo[0].advokatSomKontakt', //TODO: fix så den funker på alle
			Yup.object({
				organisasjonsnummer: Yup.string().nullable(),
				organisasjonsnavn: Yup.string().nullable(),
				kontaktperson: Yup.object({
					fornavn: requiredString.nullable(),
					mellomnavn: Yup.string().nullable(),
					etternavn: requiredString.nullable(),
				}),
			})
		),
		personSomKontakt: ifPresent(
			'$pdldata.person.kontaktinformasjonForDoedsbo[0].personSomKontakt', //TODO: fix så den funker på alle
			Yup.object({
				identifikasjonsnummer: Yup.string().nullable(),
				foedselsdato: Yup.string().nullable(),
				navn: Yup.object({
					// TODO: bare hvis ikke identifikasjonsnummer er satt
					fornavn: requiredString.nullable(),
					mellomnavn: Yup.string().nullable(),
					etternavn: requiredString.nullable(),
				}),
			})
		),
		organisasjonSomKontakt: ifPresent(
			'$pdldata.person.kontaktinformasjonForDoedsbo[0].organisasjonSomKontakt', //TODO: fix så den funker på alle
			Yup.object({
				organisasjonsnummer: Yup.string().nullable(),
				organisasjonsnavn: requiredString.nullable(),
				kontaktperson: Yup.object({
					fornavn: Yup.string().nullable(),
					mellomnavn: Yup.string().nullable(),
					etternavn: Yup.string().nullable(),
				}),
			})
		),
		// adressat: Yup.object({
		// 	// adressatType: requiredString,
		// 	kontaktperson: ifKeyHasValue(
		// 		'$pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType',
		// 		['ADVOKAT', 'ORGANISASJON'],
		// 		personnavnSchema
		// 	),
		// 	organisasjonsnavn: Yup.string().when('adressatType', {
		// 		is: 'ORGANISASJON',
		// 		then: requiredString,
		// 	}),
		// 	organisasjonsnummer: Yup.string().when('adressatType', {
		// 		is: 'ADVOKAT' || 'ORGANISASJON',
		// 		then: Yup.string().matches(/^[0-9]{9}$/, {
		// 			message: 'Organisasjonsnummeret må være et tall med 9 sifre',
		// 			excludeEmptyString: true,
		// 		}),
		// 	}),
		// 	idnummer: Yup.string().when('adressatType', {
		// 		is: 'PERSON_MEDID',
		// 		then: requiredString.matches(/^[0-9]{11}$/, {
		// 			message: 'Id-nummeret må være et tall med 11 sifre',
		// 			excludeEmptyString: true,
		// 		}),
		// 	}),
		// 	navn: ifKeyHasValue(
		// 		'$pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType',
		// 		['PERSON_UTENID'],
		// 		personnavnSchema
		// 	),
		// }),
	})
)

export const validation = {
	pdldata: Yup.object({
		person: Yup.object({
			bostedsadresse: ifPresent('$pdldata.person.bostedsadresse', bostedsadresse),
			fullmakt: ifPresent('$pdldata.person.fullmakt', fullmakt),
			falskIdentitet: ifPresent('$pdldata.person.falskIdentitet', falskIdentitet),
			utenlandskIdentifikasjonsnummer: ifPresent(
				'$pdldata.person.utenlandskIdentifikasjonsnummer',
				utenlandskId
			),
			kontaktinformasjonForDoedsbo: ifPresent(
				'$pdldata.person.kontaktinformasjonForDoedsbo',
				kontaktDoedsbo
			),
		}),
	}),
}

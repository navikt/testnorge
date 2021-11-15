import * as Yup from 'yup'
import { ifKeyHasValue, ifPresent, requiredDate, requiredString } from '~/utils/YupValidations'

const personnavnSchema = Yup.object({
	fornavn: Yup.string(),
	mellomnavn: Yup.string(),
	etternavn: Yup.string(),
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

const kontaktDoedsbo = Yup.object({
	adressat: Yup.object({
		adressatType: requiredString,
		kontaktperson: ifKeyHasValue(
			'$pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType',
			['ADVOKAT', 'ORGANISASJON'],
			personnavnSchema
		),
		organisasjonsnavn: Yup.string().when('adressatType', {
			is: 'ORGANISASJON',
			then: requiredString,
		}),
		organisasjonsnummer: Yup.string().when('adressatType', {
			is: 'ADVOKAT' || 'ORGANISASJON',
			then: Yup.string().matches(/^[0-9]{9}$/, {
				message: 'Organisasjonsnummeret må være et tall med 9 sifre',
				excludeEmptyString: true,
			}),
		}),
		idnummer: Yup.string().when('adressatType', {
			is: 'PERSON_MEDID',
			then: requiredString.matches(/^[0-9]{11}$/, {
				message: 'Id-nummeret må være et tall med 11 sifre',
				excludeEmptyString: true,
			}),
		}),
		navn: ifKeyHasValue(
			'$pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType',
			['PERSON_UTENID'],
			personnavnSchema
		),
	}),
	adresselinje1: requiredString,
	adresselinje2: Yup.string(),
	postnummer: requiredString,
	poststedsnavn: requiredString,
	landkode: requiredString,
	skifteform: requiredString,
	utstedtDato: requiredDate,
})

export const validation = {
	pdlforvalter: Yup.object({
		kontaktinformasjonForDoedsbo: ifPresent(
			'$pdlforvalter.kontaktinformasjonForDoedsbo',
			kontaktDoedsbo
		),
	}),
	pdldata: Yup.object({
		person: Yup.object({
			bostedsadresse: ifPresent('$pdldata.person.bostedsadresse', bostedsadresse),
			falskIdentitet: ifPresent('$pdldata.person.falskIdentitet', falskIdentitet),
			utenlandskIdentifikasjonsnummer: ifPresent(
				'$pdldata.person.utenlandskIdentifikasjonsnummer',
				utenlandskId
			),
		}),
	}),
}

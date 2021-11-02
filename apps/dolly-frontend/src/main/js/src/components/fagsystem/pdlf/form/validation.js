import * as Yup from 'yup'
import { ifKeyHasValue, ifPresent, requiredDate, requiredString } from '~/utils/YupValidations'

const personnavnSchema = Yup.object({
	fornavn: Yup.string(),
	mellomnavn: Yup.string(),
	etternavn: Yup.string(),
})

const falskIdentitet = Yup.array().of(
	Yup.object({
		// identitetType: requiredString,
		rettIdentErUkjent: Yup.boolean(),
		rettIdentitetVedIdentifikasjonsnummer: Yup.string().nullable(),
		rettIdentitetVedOpplysninger: Yup.object({
			foedselsdato: Yup.string().nullable(),
			kjoenn: Yup.string().nullable(),
			personnavn: personnavnSchema.nullable(),
			statsborgerskap: Yup.array().of(Yup.string()),
		}),
		// personnavn: ifKeyHasValue(
		// 	'$pdlforvalter.falskIdentitet.rettIdentitet.identitetType',
		// 	['OMTRENTLIG'],
		// 	personnavnSchema
		// ),
		// foedselsdato: Yup.mixed().when('identitetType', {
		// 	is: 'OMTRENTLIG',
		// 	then: requiredDate,
		// }),
		// kjoenn: Yup.mixed().when('identitetType', {
		// 	is: 'OMTRENTLIG',
		// 	then: requiredString,
		// }),
		// statsborgerskap: Yup.mixed().when('identitetType', {
		// 	is: 'OMTRENTLIG',
		// 	then: requiredString,
		// }),
	})
)

const utenlandskId = Yup.array().of(
	Yup.object({
		identifikasjonsnummer: requiredString,
		// kilde: requiredString,
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
			falskIdentitet: ifPresent('$pdldata.person.falskIdentitet', falskIdentitet),
			utenlandskIdentifikasjonsnummer: ifPresent(
				'$pdldata.person.utenlandskIdentifikasjonsnummer',
				utenlandskId
			),
		}),
	}),
}

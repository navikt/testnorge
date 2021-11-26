import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '~/utils/YupValidations'

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
	Yup.object().shape({
		skifteform: requiredString.nullable(),
		attestutstedelsesdato: requiredDate,
		kontaktType: requiredString.nullable(),
		adresse: Yup.object({
			adresselinje1: Yup.string().nullable(),
			adresselinje2: Yup.string().nullable(),
			postnummer: Yup.string().nullable(),
			poststedsnavn: Yup.string().nullable(),
			landkode: Yup.string().nullable(),
		}),

		advokatSomKontakt: Yup.object().when('kontaktType', {
			is: 'ADVOKAT',
			then: Yup.object({
				organisasjonsnummer: requiredString.nullable(),
				organisasjonsnavn: Yup.string().nullable(),
				kontaktperson: Yup.object({
					fornavn: Yup.string().nullable(),
					mellomnavn: Yup.string().nullable(),
					etternavn: Yup.string().nullable(),
				}).nullable(),
			}),
		}),

		organisasjonSomKontakt: Yup.object().when('kontaktType', {
			is: 'ORGANISASJON',
			then: Yup.object({
				organisasjonsnummer: requiredString.nullable(),
				organisasjonsnavn: Yup.string().nullable(),
				kontaktperson: Yup.object({
					fornavn: Yup.string().nullable(),
					mellomnavn: Yup.string().nullable(),
					etternavn: Yup.string().nullable(),
				}).nullable(),
			}),
		}),

		personSomKontakt: Yup.object()
			.when('kontaktType', {
				is: 'PERSON_FDATO',
				then: Yup.object({
					foedselsdato: requiredString.nullable(),
					navn: Yup.object({
						fornavn: Yup.string().nullable(),
						mellomnavn: Yup.string().nullable(),
						etternavn: Yup.string().nullable(),
					}).nullable(),
				}),
			})
			.when('kontaktType', {
				is: 'NY_PERSON',
				then: Yup.object({
					nyKontaktperson: nyPerson,
				}),
			}),
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

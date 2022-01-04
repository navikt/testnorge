import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '~/utils/YupValidations'
import _get from 'lodash/get'

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

const testTelefonnummer = () =>
	Yup.string()
		.max(20, 'Telefonnummer kan ikke ha mer enn 20 sifre')
		.when('landskode', {
			is: '+47',
			then: Yup.string().length(8, 'Norsk telefonnummer må ha 8 sifre'),
		})
		.required('Feltet er påkrevd')
		.matches(/^[1-9]\d*$/, 'Telefonnummer må være numerisk, og kan ikke starte med 0')

const testPrioritet = (val) => {
	return val.test('prioritet', 'Kan ikke ha lik prioritet', function erEgenPrio() {
		const values = this.options.context
		const index = this.options.index
		const tlfListe = _get(values, 'pdldata.person.telefonnummer')
		if (tlfListe.length < 2) return true
		const index2 = index === 0 ? 1 : 0
		return tlfListe[index]?.prioritet !== tlfListe[index2]?.prioritet
	})
}

const telefonnummer = Yup.array().of(
	Yup.object({
		landskode: requiredString,
		nummer: testTelefonnummer(),
		prioritet: testPrioritet(requiredString).nullable(),
	})
)

const innflytting = Yup.array().of(
	Yup.object({
		fraflyttingsland: requiredString,
		fraflyttingsstedIUtlandet: Yup.string().optional().nullable(),
		innflyttingsdato: requiredDate.nullable(),
	})
)

const utflytting = Yup.array().of(
	Yup.object({
		tilflyttingsland: requiredString,
		tilflyttingsstedIUtlandet: Yup.string().optional().nullable(),
		utflyttingsdato: requiredDate.nullable(),
	})
)

export const validation = {
	pdldata: Yup.object({
		person: Yup.object({
			bostedsadresse: ifPresent('$pdldata.person.bostedsadresse', bostedsadresse),
			fullmakt: ifPresent('$pdldata.person.fullmakt', fullmakt),
			falskIdentitet: ifPresent('$pdldata.person.falskIdentitet', falskIdentitet),
			telefonnummer: ifPresent('$pdldata.person.telefonnummer', telefonnummer),
			innflytting: ifPresent('$pdldata.person.innflytting', innflytting),
			utflytting: ifPresent('$pdldata.person.utflytting', utflytting),
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

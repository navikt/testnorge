import * as Yup from 'yup'
import { ifKeyHasValue, ifPresent, requiredDate, requiredString } from '~/utils/YupValidations'
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

const testTelefonnummer = () =>
	Yup.string()
		.max(20, 'Telefonnummer kan ikke ha mer enn 20 sifre')
		// TODO: Fiks denne med context!
		// .when(`telefonLandskode_${nr}`, {
		// 	is: '+47',
		// 	then: Yup.string().length(8, 'Norsk telefonnummer må ha 8 sifre'),
		// })
		.required('Feltet er påkrevd')
		.matches(/^[1-9][0-9]*$/, 'Telefonnummer må være numerisk, og kan ikke starte med 0')

const testPrioritet = (validation) => {
	return validation.test('prioritet', 'Kan kun brukes en gang', function erEgenPrio(val) {
		const values = this.options.context
		const index = this.options.index
		const tlfListe = _get(values, 'pdldata.person.telefonnummer')

		// TODO: Sjekk at prio er 1 når lengde er 1
		if (tlfListe.length < 2) return true

		const index2 = index === 0 ? 1 : 0
		if (tlfListe[index].prioritet === tlfListe[index2].prioritet) return false
		return true
	})
}

const telefonnummer = Yup.array().of(
	Yup.object({
		landskode: requiredString,
		nummer: testTelefonnummer(),
		prioritet: testPrioritet(requiredString).nullable(),
	})
)

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
			fullmakt: ifPresent('$pdldata.person.fullmakt', fullmakt),
			falskIdentitet: ifPresent('$pdldata.person.falskIdentitet', falskIdentitet),
			telefonnummer: ifPresent('$pdldata.person.telefonnummer', telefonnummer),
			utenlandskIdentifikasjonsnummer: ifPresent(
				'$pdldata.person.utenlandskIdentifikasjonsnummer',
				utenlandskId
			),
		}),
	}),
}

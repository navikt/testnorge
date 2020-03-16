import * as Yup from 'yup'
import { requiredString, requiredDate, ifKeyHasValue, ifPresent } from '~/utils/YupValidations'

const falskIdentitet = Yup.object({
	rettIdentitet: Yup.object({
		identitetType: requiredString,
		rettIdentitetVedIdentifikasjonsnummer: Yup.mixed().when('identitetType', {
			is: 'ENTYDIG',
			then: requiredString
		}),
		personnavn: ifKeyHasValue(
			'$pdlforvalter.falskIdentitet.rettIdentitet.identitetType',
			['OMTRENTLIG'],
			requiredString
		),
		foedselsdato: Yup.mixed().when('identitetType', {
			is: 'OMTRENTLIG',
			then: requiredDate
		}),
		kjoenn: Yup.mixed().when('identitetType', {
			is: 'OMTRENTLIG',
			then: requiredString
		}),
		statsborgerskap: Yup.mixed().when('identitetType', {
			is: 'OMTRENTLIG',
			then: requiredString
		})
	})
})

const utenlandskId = Yup.array().of(
	Yup.object({
		identifikasjonsnummer: requiredString,
		kilde: requiredString,
		opphoert: requiredString,
		utstederland: requiredString
	})
)

const kontaktDoedsbo = Yup.object({
	adressat: Yup.object({
		adressatType: requiredString,
		kontaktperson: ifKeyHasValue(
			'$pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType',
			['ADVOKAT', 'ORGANISASJON'],
			requiredString
		),
		organisasjonsnavn: Yup.string().when('adressatType', {
			is: 'ORGANISASJON',
			then: requiredString
		}),
		organisasjonsnummer: Yup.string().when('adressatType', {
			is: 'ADVOKAT' || 'ORGANISASJON',
			then: Yup.string().matches(/^[0-9]{9}$/, {
				message: 'Organisasjonsnummeret må være et tall med 9 sifre',
				excludeEmptyString: true
			})
		}),
		idnummer: Yup.string().when('adressatType', {
			is: 'PERSON_MEDID',
			then: requiredString
		}),
		navn: ifKeyHasValue(
			'$pdlforvalter.kontaktinformasjonForDoedsbo.adressat.adressatType',
			['PERSON_UTENID'],
			requiredString
		)
	}),
	adresselinje1: requiredString,
	adresselinje2: Yup.string(),
	postnummer: requiredString,
	poststedsnavn: requiredString,
	landkode: requiredString,
	skifteform: requiredString,
	utstedtDato: requiredDate
})

export const validation = {
	pdlforvalter: Yup.object({
		falskIdentitet: ifPresent('$pdlforvalter.falskIdentitet', falskIdentitet),
		utenlandskIdentifikasjonsnummer: ifPresent(
			'$pdlforvalter.utenlandskIdentifikasjonsnummer',
			utenlandskId
		),
		kontaktinformasjonForDoedsbo: ifPresent(
			'$pdlforvalter.kontaktinformasjonForDoedsbo',
			kontaktDoedsbo
		)
	})
}

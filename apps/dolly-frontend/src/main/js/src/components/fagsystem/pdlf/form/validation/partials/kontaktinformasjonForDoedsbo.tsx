import * as Yup from 'yup'
import { requiredDate, requiredString } from '@/utils/YupValidations'
import { nyPerson } from '@/components/fagsystem/pdlf/form/validation/partials'

export const kontaktDoedsbo = Yup.object().shape({
	skifteform: requiredString,
	attestutstedelsesdato: requiredDate.nullable(),
	kontaktType: requiredString,
	adresse: Yup.object({
		adresselinje1: Yup.string().nullable(),
		adresselinje2: Yup.string().nullable(),
		postnummer: Yup.string().nullable(),
		poststedsnavn: Yup.string().nullable(),
		landkode: Yup.string().nullable(),
	}),

	advokatSomKontakt: Yup.object()
		.when('kontaktType', {
			is: 'ADVOKAT',
			then: () =>
				Yup.object({
					organisasjonsnummer: requiredString,
					organisasjonsnavn: Yup.string().nullable(),
					kontaktperson: Yup.object({
						fornavn: Yup.string().nullable(),
						mellomnavn: Yup.string().nullable(),
						etternavn: Yup.string().nullable(),
					}).nullable(),
				}),
		})
		.nullable(),

	organisasjonSomKontakt: Yup.object()
		.when('kontaktType', {
			is: 'ORGANISASJON',
			then: () =>
				Yup.object({
					organisasjonsnummer: requiredString,
					organisasjonsnavn: Yup.string().nullable(),
					kontaktperson: Yup.object({
						fornavn: Yup.string().nullable(),
						mellomnavn: Yup.string().nullable(),
						etternavn: Yup.string().nullable(),
					}).nullable(),
				}),
		})
		.nullable(),

	personSomKontakt: Yup.mixed()
		.when('kontaktType', {
			is: 'PERSON_FDATO',
			then: () =>
				Yup.object().shape(
					{
						identifikasjonsnummer: Yup.mixed()
							.when('foedselsdato', {
								is: null,
								then: () => requiredString,
							})
							.nullable(),
						foedselsdato: Yup.mixed()
							.when('identifikasjonsnummer', {
								is: null,
								then: () => requiredDate.nullable(),
							})
							.nullable(),
						navn: Yup.object({
							fornavn: Yup.string().nullable(),
							mellomnavn: Yup.string().nullable(),
							etternavn: Yup.string().nullable(),
						}).nullable(),
					},
					['identifikasjonsnummer', 'foedselsdato']
				),
		})
		.nullable()
		.when('kontaktType', {
			is: 'NY_PERSON',
			then: () =>
				Yup.object({
					nyKontaktperson: nyPerson,
				}),
		})
		.nullable(),
})

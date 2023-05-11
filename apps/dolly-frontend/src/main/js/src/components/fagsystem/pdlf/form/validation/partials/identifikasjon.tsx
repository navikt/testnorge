import * as Yup from 'yup'
import { ifNotBlank, requiredString } from '@/utils/YupValidations'

const personnavnSchema = Yup.object({
	fornavn: ifNotBlank('$pdldata.person.navn[0].etternavn', requiredString),
	mellomnavn: Yup.string().nullable(),
	etternavn: ifNotBlank('$pdldata.person.navn[0].fornavn', requiredString),
})

export const falskIdentitet = Yup.array().of(
	Yup.object({
		rettIdentErUkjent: Yup.boolean(),
		rettIdentitetVedIdentifikasjonsnummer: Yup.string().nullable(),
		rettIdentitetVedOpplysninger: Yup.object({
			foedselsdato: Yup.string().nullable(),
			kjoenn: Yup.string().nullable(),
			personnavn: personnavnSchema.nullable(),
			statsborgerskap: Yup.array().of(Yup.string()),
		}).nullable(),
	})
)

export const utenlandskId = Yup.array().of(
	Yup.object({
		identifikasjonsnummer: requiredString,
		opphoert: requiredString,
		utstederland: requiredString,
	})
)

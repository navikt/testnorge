import { ifPresent, messages, requiredNumber, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'

const grunnlagValidation = Yup.object({
	tekniskNavn: requiredString,
	kategori: Yup.string().nullable(),
	andelOverfoertFraBarn: Yup.number().nullable().integer().positive(messages.positive),
	beloep: requiredNumber.positive(messages.positive),
	spesifisering: Yup.array().of(
		Yup.object({
			type: requiredString,
			aarForFoerstegangsregistrering: Yup.number()
				.nullable()
				.positive(messages.positive)
				.min(1900, 'Valgt år må være etter 1900')
				.max(new Date().getFullYear(), 'Valgt år må være senest i år'),
			antattMarkedsverdi: Yup.number().nullable().integer(),
			antattVerdiSomNytt: Yup.number().nullable().integer(),
			beloep: requiredNumber.positive(messages.positive),
			eierandel: Yup.number().nullable().integer().positive(messages.positive),
			fabrikatnavn: Yup.string().nullable(),
			formuesverdi: Yup.number().nullable().integer().positive(messages.positive),
			formuesverdiForFormuesandel: Yup.number().nullable().integer().positive(messages.positive),
			registreringsnummer: Yup.string().nullable(),
		}).nullable(),
	),
})

export const validation = {
	sigrunstubSummertSkattegrunnlag: ifPresent(
		'$sigrunstubSummertSkattegrunnlag',
		Yup.array().of(
			Yup.object({
				inntektsaar: requiredString,
				stadie: requiredString,
				ajourholdstidspunkt: Yup.date().nullable(),
				skatteoppgjoersdato: Yup.date().nullable(),
				skjermet: Yup.boolean().nullable(),
				grunnlag: Yup.array().of(grunnlagValidation).nullable(),
				kildeskattPaaLoennGrunnlag: Yup.array().of(grunnlagValidation).nullable(),
				svalbardGrunnlag: Yup.array().of(grunnlagValidation).nullable(),
			}),
		),
	),
}

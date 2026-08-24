import { ifPresent, messages, requiredNumber, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'

const nullableNumber = () =>
	Yup.number()
		.transform((i, j) => (j === '' ? null : i))
		.nullable()

const grunnlagValidation = Yup.object({
	tekniskNavn: requiredString,
	kategori: Yup.string().nullable(),
	andelOverfoertFraBarn: nullableNumber().integer().positive(messages.positive),
	beloep: requiredNumber.positive(messages.positive).transform((i, j) => (j === '' ? null : i)),
	spesifisering: Yup.array().of(
		Yup.object({
			type: requiredString,
			aarForFoerstegangsregistrering: nullableNumber()
				.positive(messages.positive)
				.min(1900, 'Valgt år må være etter 1900')
				.max(new Date().getFullYear(), 'Valgt år må være senest i år'),
			antattMarkedsverdi: nullableNumber().integer(),
			antattVerdiSomNytt: nullableNumber().integer(),
			beloep: requiredNumber.transform((i, j) => (j === '' ? null : i)).positive(messages.positive),
			eierandel: nullableNumber().integer().positive(messages.positive),
			fabrikatnavn: Yup.string().nullable(),
			formuesverdi: nullableNumber().integer().positive(messages.positive),
			formuesverdiForFormuesandel: nullableNumber().integer().positive(messages.positive),
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

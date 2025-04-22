import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'

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
				grunnlag: Yup.array()
					.of(
						Yup.object({
							tekniskNavn: requiredString,
							kategori: Yup.string().nullable(),
							andelOverfoertFraBarn: Yup.number().nullable().integer().positive(),
							beloep: Yup.number().nullable().integer().positive(),
							spesifisering: Yup.array().of(
								Yup.object({
									type: requiredString,
									aarForFoerstegangsregistrering: Yup.string().nullable(),
									antattMarkedsverdi: Yup.number().nullable().integer(),
									antattVerdiSomNytt: Yup.number().nullable().integer(),
									beloep: Yup.number().nullable().integer().positive(),
									eierandel: Yup.number().nullable().integer().positive(),
									fabrikatnavn: Yup.string().nullable(),
									formuesverdi: Yup.number().nullable().integer().positive(),
									formuesverdiForFormuesandel: Yup.number().nullable().integer().positive(),
									registreringsnummer: Yup.string().nullable(),
								}).nullable(),
							),
						}).nullable(),
					)
					.nullable(),
			}),
		),
	),
}

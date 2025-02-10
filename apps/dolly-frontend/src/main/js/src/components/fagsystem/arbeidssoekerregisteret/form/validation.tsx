import * as Yup from 'yup'
import { ifPresent } from '@/utils/YupValidations'

const dato = Yup.date()
	.transform((i, j) => (j === '' ? null : i))
	.typeError('Ugyldig dato-format')
	.nullable()

export const validation = {
	arbeidssoekerregisteret: ifPresent(
		'$arbeidssoekerregisteret',
		Yup.object({
			utfoertAv: Yup.string().nullable(),
			kilde: Yup.string().nullable(),
			aarsak: Yup.string().nullable(),
			nuskode: Yup.string().nullable(),
			utdanningBestaatt: Yup.boolean().required(),
			utdanningGodkjent: Yup.boolean().required(),
			jobbsituasjonsbeskrivelse: Yup.string().nullable(),
			jobbsituasjonsdetaljer: Yup.object({
				gjelderFraDato: dato,
				gjelderTilDato: dato,
				stillingStyrk08: Yup.string().nullable(),
				stillingstittel: Yup.string().nullable(),
				stillingsprosent: Yup.number()
					.transform((i, j) => (j === '' ? null : i))
					.typeError('Må være et nummer')
					.nullable(),
				sisteDagMedLoenn: dato,
				sisteArbeidsdag: dato,
			}),
			helsetilstandHindrerArbeid: Yup.boolean().required(),
			andreForholdHindrerArbeid: Yup.boolean().required(),
		}),
	),
}

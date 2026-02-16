import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'

export const validation = {
	skattekort: ifPresent(
		'$skattekort',
		Yup.object({
			arbeidsgiverSkatt: Yup.array().of(
				Yup.object({
					arbeidstaker: Yup.array().of(
						Yup.object({
							resultatPaaForespoersel: requiredString,
							skattekort: Yup.object({
								utstedtDato: Yup.string().nullable(),
								skattekortidentifikator: Yup.string().nullable(),
								forskuddstrekk: Yup.array().of(
									Yup.object({
										frikort: ifPresent(
											'$frikort',
											Yup.object({
												trekkode: requiredString,
												frikortbeloep: Yup.number()
													.transform((i, j) => (j === '' ? null : i))
													.min(1, 'Kan ikke være mindre enn ${min}')
													.max(1000000, 'Kan ikke være større enn ${max}')
													.required('Frikortbeløp er påkrevd'),
											}),
										),
										trekktabell: ifPresent(
											'$trekktabell',
											Yup.object({
												trekkode: requiredString,
												tabellnummer: Yup.number()
													.min(1000, 'Kan ikke være mindre enn ${min}')
													.max(9999, 'Kan ikke være større enn ${max}')
													.required('Tabellnummer er påkrevd'),
												prosentsats: Yup.number()
													.transform((i, j) => (j === '' ? null : i))
													.min(0, 'Kan ikke være mindre enn ${min}')
													.max(100, 'Kan ikke være større enn ${max}')
													.required('Prosentsats er påkrevd'),
												antallMaanederForTrekk: Yup.number()
													.transform((i, j) => (j === '' ? null : i))
													.min(1, 'Kan ikke være mindre enn ${min}')
													.max(12, 'Kan ikke være større enn ${max}')
													.required('Antall måneder for trekk er påkrevd'),
											}),
										),
										trekkprosent: ifPresent(
											'$trekkprosent',
											Yup.object({
												trekkode: requiredString,
												prosentsats: Yup.number()
													.transform((i, j) => (j === '' ? null : i))
													.min(0, 'Kan ikke være mindre enn ${min}')
													.max(100, 'Kan ikke være større enn ${max}')
													.required('Prosentsats er påkrevd'),
												antallMaanederForTrekk: Yup.number()
													.transform((i, j) => (j === '' ? null : i))
													.min(1, 'Kan ikke være mindre enn ${min}')
													.max(12, 'Kan ikke være større enn ${max}')
													.nullable(),
											}),
										),
									}),
								),
							}),
							tilleggsopplysning: Yup.array().of(Yup.string().nullable()),
							inntektsaar: requiredString,
						}),
					),
				}),
			),
		}),
	),
}

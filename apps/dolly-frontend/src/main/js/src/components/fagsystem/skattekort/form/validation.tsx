import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'

export const validation = {
	skattekort: ifPresent(
		'$skattekort',
		Yup.object({
			arbeidsgiverSkatt: Yup.array().of(
				Yup.object({
					arbeidsgiveridentifikator: Yup.object({
						organisasjonsnummer: ifPresent(
							'$organisasjonsnummer',
							requiredString
								.matches(/^\d*$/, 'Orgnummer må være et tall med 9 sifre')
								.test('len', 'Orgnummer må være et tall med 9 sifre', (val) => val?.length === 9),
						),
						personidentifikator: ifPresent(
							'$personidentifikator',
							requiredString
								.matches(/^\d*$/, 'Ident må være et tall med 11 sifre')
								.test('len', 'Ident må være et tall med 11 sifre', (val) => val?.length === 11),
						),
					}),
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
												frikortbeloep: Yup.string().nullable(),
											}),
										),
										trekktabell: ifPresent(
											'$trekktabell',
											Yup.object({
												trekkode: requiredString,
												tabellnummer: Yup.string().nullable(),
												prosentsats: Yup.number()
													.transform((i, j) => (j === '' ? null : i))
													.min(0, 'Kan ikke være mindre enn ${min}')
													.max(100, 'Kan ikke være større enn ${max}')
													.nullable(),
												antallMaanederForTrekk: Yup.string().nullable(),
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
													.nullable(),
												antallMaanederForTrekk: Yup.string().nullable(),
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

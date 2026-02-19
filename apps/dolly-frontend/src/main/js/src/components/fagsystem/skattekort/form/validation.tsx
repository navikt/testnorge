import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'

const uniqueTrekkodeTest = (val: string, context: Yup.TestContext<any>) => {
	const forskuddstrekk = context.from[2].value?.forskuddstrekk || []
	return (
		forskuddstrekk.reduce((counter: number, entry: any) => {
			return (
				counter +
				(entry.frikort?.trekkode === val ? 1 : 0) +
				(entry.trekktabell?.trekkode === val ? 1 : 0) +
				(entry.trekkprosent?.trekkode === val ? 1 : 0)
			)
		}, 0) < 2
	)
}

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
												trekkode: Yup.string()
													.required()
													.test('isFrikort', 'Trekkode må være unik', (val, context) =>
														uniqueTrekkodeTest(val, context),
													),
												frikortbeloep: Yup.number()
													.transform((i, j) => (j === '' ? null : i))
													.min(1, 'Kan ikke være mindre enn ${min}')
													.max(1000000, 'Kan ikke være større enn ${max}')
													.nullable(),
											}),
										),
										trekktabell: ifPresent(
											'$trekktabell',
											Yup.object({
												trekkode: Yup.string()
													.required()
													.test('isTrekktabell', 'Trekkode må være unik', (val, context) =>
														uniqueTrekkodeTest(val, context),
													),
												tabellnummer: Yup.number()
													.min(7000, 'Kan ikke være mindre enn ${min}')
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
												trekkode: Yup.string()
													.required()
													.test('isTrekkprosent', 'Trekkode må være unik', (val, context) =>
														uniqueTrekkodeTest(val, context),
													),
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
							tilleggsopplysning: Yup.array()
								.of(Yup.string())
								.test(
									'isRequired',
									'Velg minst et forskuddstrekk eller tilleggsopplysning ("Opphold på Svalbard", "Kildeskatt på pensjon")',
									(tilleggsopplysninger: any, context) => {
										console.log('context', context.parent)
										console.log('Lengde', context.parent?.skattekort?.forskuddstrekk?.length)
										console.log('val', tilleggsopplysninger)
										return (
											context.parent.resultatPaaForespoersel !== 'SKATTEKORTOPPLYSNINGER_OK' ||
											tilleggsopplysninger?.find(
												(opplysning) =>
													opplysning === 'OPPHOLD_PAA_SVALBARD' ||
													opplysning === 'KILDESKATT_PAA_PENSJON',
											) ||
											context.parent?.skattekort?.forskuddstrekk?.length > 0
										)
									},
								)
								.nullable(),
							inntektsaar: requiredString,
						}),
					),
				}),
			),
		}),
	),
}

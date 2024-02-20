import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredNumber, requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

export const validation = {
	arbeidsplassenCV: ifPresent(
		'$arbeidsplassenCV',
		Yup.object({
			jobboensker: ifPresent(
				'$arbeidsplassenCV.jobboensker',
				Yup.object({
					occupations: Yup.array().of(Yup.object()).min(1, 'Velg minst ett yrke'),
					locations: Yup.array().of(Yup.object()).min(1, 'Velg minst ett område'),
				}),
			),
			utdanning: ifPresent(
				'$arbeidsplassenCV.utdanning',
				Yup.array().of(
					Yup.object({
						nuskode: requiredString,
						startDate: testDatoFom(requiredDate, 'endDate', 'Dato må være før sluttdato'),
						endDate: Yup.mixed()
							.when('ongoing', {
								is: false,
								then: () => testDatoTom(requiredDate, 'startDate', 'Dato må være etter startdato'),
							})
							.nullable(),
					}),
				),
			),
			fagbrev: ifPresent(
				'$arbeidsplassenCV.fagbrev',
				Yup.array().of(
					Yup.object({
						title: requiredString,
					}),
				),
			),
			arbeidserfaring: ifPresent(
				'$arbeidsplassenCV.arbeidserfaring',
				Yup.array().of(
					Yup.object({
						styrkkode: requiredString,
						fromDate: testDatoFom(requiredDate.nullable(), 'toDate', 'Dato må være før tildato'),
						toDate: Yup.mixed()
							.when('ongoing', {
								is: false,
								then: () =>
									testDatoTom(requiredDate.nullable(), 'fromDate', 'Dato må være etter fradato'),
							})
							.nullable(),
					}),
				),
			),
			annenErfaring: ifPresent(
				'$arbeidsplassenCV.annenErfaring',
				Yup.array().of(
					Yup.object({
						role: requiredString,
					}),
				),
			),
			kompetanser: ifPresent(
				'$arbeidsplassenCV.kompetanser',
				Yup.array().of(
					Yup.object({
						title: requiredString,
					}),
				),
			),
			offentligeGodkjenninger: ifPresent(
				'$arbeidsplassenCV.offentligeGodkjenninger',
				Yup.array().of(
					Yup.object({
						title: requiredString,
						fromDate: testDatoFom(requiredDate.nullable(), 'toDate', 'Dato må være før utløpsdato'),
						toDate: testDatoTom(
							Yup.date().nullable(),
							'fromDate',
							'Dato må være etter fullført dato',
						),
					}),
				),
			),
			andreGodkjenninger: ifPresent(
				'$arbeidsplassenCV.andreGodkjenninger',
				Yup.array().of(
					Yup.object({
						certificateName: requiredString,
						fromDate: testDatoFom(requiredDate.nullable(), 'toDate', 'Dato må være før utløpsdato'),
						toDate: testDatoTom(
							Yup.date().nullable(),
							'fromDate',
							'Dato må være etter fullført dato',
						),
					}),
				),
			),
			spraak: ifPresent(
				'$arbeidsplassenCV.spraak',
				Yup.array().of(
					Yup.object({
						language: requiredString,
					}),
				),
			),
			foererkort: ifPresent(
				'$arbeidsplassenCV.foererkort',
				Yup.array().of(
					Yup.object({
						type: requiredString,
					}),
				),
			),
			kurs: ifPresent(
				'$arbeidsplassenCV.kurs',
				Yup.array().of(
					Yup.object({
						title: requiredString,
						duration: Yup.mixed().when('durationUnit', {
							is: (val) => val !== null,
							then: () => requiredString || requiredNumber,
						}),
					}),
				),
			),
			sammendrag: ifPresent('$arbeidsplassenCV.sammendrag', requiredString),
		}),
	),
}

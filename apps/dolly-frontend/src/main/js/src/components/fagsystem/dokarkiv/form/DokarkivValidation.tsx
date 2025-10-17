import * as Yup from 'yup'
import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as _ from 'lodash-es'

export const dokarkivValidation = {
	dokarkiv: ifPresent(
		'$dokarkiv',
		Yup.array().of(
			Yup.object({
				tittel: requiredString,
				tema: requiredString,
				journalfoerendeEnhet: Yup.string()
					.optional()
					.nullable()
					.matches(/^\d*$/, 'Journalfoerende enhet må enten være blank eller et tall med 4 sifre')
					.test(
						'len',
						'Journalfoerende enhet må enten være blank eller et tall med 4 sifre',
						(val) => !val || (val && val.length === 4),
					),
				sak: Yup.object({
					sakstype: requiredString,
					fagsaksystem: Yup.string().when('sakstype', {
						is: 'FAGSAK',
						then: () => requiredString,
						otherwise: () => Yup.mixed().notRequired(),
					}),
					fagsakId: Yup.string().when('sakstype', {
						is: 'FAGSAK',
						then: () => requiredString,
						otherwise: () => Yup.mixed().notRequired(),
					}),
				}),
				avsenderMottaker: Yup.object({
					idType: Yup.string().optional().nullable(),
					id: Yup.string()
						.when('idType', {
							is: 'ORGNR',
							then: () =>
								Yup.string()
									.matches(/^\d*$/, 'Orgnummer må være et tall med 9 sifre')
									.test(
										'len',
										'Orgnummer må være et tall med 9 sifre',
										(val) => val && val.length === 9,
									),
						})
						.when('idType', {
							is: 'FNR',
							then: () =>
								Yup.string()
									.matches(/^\d*$/, 'Ident må være et tall med 11 sifre')
									.test(
										'len',
										'Ident må være et tall med 11 sifre',
										(val) => val && val.length === 11,
									),
						}),
					navn: Yup.string().optional(),
				}),
				dokumenter: Yup.array().of(
					Yup.object({
						tittel: requiredString,
						brevkode: Yup.string().test(
							'is-valid-brevkode',
							'Feltet er påkrevd',
							(_val, testContext) => {
								const fullForm =
									testContext.from && testContext.from[testContext.from.length - 1]?.value
								const brevkode = _.get(fullForm, 'dokarkiv.dokumenter[0].brevkode')
								return brevkode !== ''
							},
						),
					}),
				),
			}),
		),
	),
}

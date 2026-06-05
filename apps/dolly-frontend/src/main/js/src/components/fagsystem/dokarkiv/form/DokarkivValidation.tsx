import * as Yup from 'yup'
import { ifPresent } from '@/utils/YupValidations'

export const dokarkivValidation = {
	dokarkiv: ifPresent(
		'$dokarkiv',
		Yup.array().of(
			Yup.object({
				tittel: Yup.string().required('Tittel er påkrevd'),
				tema: Yup.string().required('Tema er påkrevd'),
				journalfoerendeEnhet: Yup.string().optional().nullable(),
				sak: Yup.object({
					sakstype: Yup.string().required('Sakstype er påkrevd'),
					fagsaksystem: Yup.string().when('sakstype', {
						is: 'FAGSAK',
						then: () => Yup.string().required('Fagsaksystem er påkrevd'),
						otherwise: () => Yup.mixed().notRequired(),
					}),
					fagsakId: Yup.string().when('sakstype', {
						is: 'FAGSAK',
						then: () => Yup.string().required('Fagsak-ID er påkrevd'),
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
						tittel: Yup.string().when('dokumentvarianter', {
							is: (val: any) => val && val.length > 0,
							then: () => Yup.string().required('Dokumenttittel er påkrevd'),
							otherwise: () => Yup.string().optional().nullable(),
						}),
						brevkode: Yup.string().optional().nullable(),
					}),
				),
			}),
		),
	),
}

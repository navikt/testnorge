import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'

export const validation = {
	instdataKdi: ifPresent(
		'$instdataKdi',
		Yup.object({
			innsettelse: ifPresent(
				'$innsettelse',
				Yup.array().of(
					Yup.object({
						meldingId: Yup.string().nullable(),
						hendelseId: Yup.string().nullable(),
						publiseringstidspunkt: requiredDate,
						kategori: requiredString,
						organisasjonsnummer: requiredString,
						tidspunkt: requiredDate,
					}),
				),
			),
			loeslatelse: ifPresent(
				'$loeslatelse',
				Yup.array().of(
					Yup.object({
						meldingId: Yup.string().nullable(),
						hendelseId: Yup.string().nullable(),
						publiseringstidspunkt: requiredDate,
						kategori: requiredString,
						organisasjonsnummer: requiredString,
						tidspunkt: requiredDate,
						erOverfoertTilUtlandskfengsel: Yup.boolean(),
						erOverfoertTilVaretektMedElektroniskKontroll: Yup.boolean(),
					}),
				),
			),
			avbruddStart: ifPresent(
				'$avbruddStart',
				Yup.array().of(
					Yup.object({
						meldingId: Yup.string().nullable(),
						hendelseId: Yup.string().nullable(),
						publiseringstidspunkt: requiredDate,
						kategori: requiredString,
						organisasjonsnummer: requiredString,
						tidspunkt: requiredDate,
						forventetAvbruddSluttTidspunkt: Yup.date().nullable(),
					}),
				),
			),
			avbruddSlutt: ifPresent(
				'$avbruddSlutt',
				Yup.array().of(
					Yup.object({
						meldingId: Yup.string().nullable(),
						hendelseId: Yup.string().nullable(),
						publiseringstidspunkt: requiredDate,
						kategori: requiredString,
						organisasjonsnummer: requiredString,
						tidspunkt: requiredDate,
					}),
				),
			),
			forventetLoeslatelse: ifPresent(
				'$forventetLoeslatelse',
				Yup.array().of(
					Yup.object({
						meldingId: Yup.string().nullable(),
						hendelseId: Yup.string().nullable(),
						publiseringstidspunkt: requiredDate,
						innmeldingHendelseId: Yup.string().nullable(),
						tidspunkt: Yup.date().nullable(),
					}),
				),
			),
			annullering: ifPresent(
				'$annullering',
				Yup.array().of(
					Yup.object({
						meldingId: Yup.string().nullable(),
						hendelseId: Yup.string().nullable(),
						publiseringstidspunkt: requiredDate,
						annullertMeldingId: Yup.string().nullable(),
					}),
				),
			),
		}),
	),
}

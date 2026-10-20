import * as Yup from 'yup'
import { ifPresent, requiredString } from '@/utils/YupValidations'

const samtalereferatFelter = {
	temagruppe: requiredString,
	tema: Yup.string().nullable(),
	enhet: Yup.string().nullable(),
	fritekst: requiredString,
	kjedeId: Yup.string().nullable(),
	meldingsId: Yup.string().nullable(),
}

export const validation = {
	henvendelse: ifPresent(
		'$henvendelse',
		Yup.object({
			meldinger: ifPresent(
				'$henvendelse.meldinger',
				Yup.array().of(
					Yup.object({
						...samtalereferatFelter,
						tildelMeg: Yup.boolean().nullable(),
					}),
				),
			),
			samtalereferater: ifPresent(
				'$henvendelse.samtalereferater',
				Yup.array().of(Yup.object(samtalereferatFelter)),
			),
		}),
	),
}

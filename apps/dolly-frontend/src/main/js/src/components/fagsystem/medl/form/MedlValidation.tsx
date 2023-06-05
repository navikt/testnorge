import * as Yup from 'yup'
import { ifPresent, requiredBoolean, requiredDate, requiredString } from '@/utils/YupValidations'
import { MEDL_KILDER } from '@/components/fagsystem/medl/MedlConstants'

export const MedlValidation = {
	medl: ifPresent(
		'$medl',
		Yup.object({
			fraOgMed: requiredDate,
			tilOgMed: requiredDate,
			status: requiredString,
			grunnlag: Yup.string().when('kilde', {
				is: (kilde) => kilde !== MEDL_KILDER.LAANEKASSEN,
				then: () => requiredString,
				otherwise: () => Yup.mixed().nullable(),
			}),
			dekning: Yup.string().when('kilde', {
				is: (kilde) => kilde !== MEDL_KILDER.LAANEKASSEN,
				then: () => requiredString,
				otherwise: () => Yup.mixed().nullable(),
			}),
			lovvalgsland: Yup.string().when('kilde', {
				is: (kilde) => kilde === MEDL_KILDER.SRVGOSYS || kilde === MEDL_KILDER.SRVMELOSYS,
				then: () => requiredString,
				otherwise: () => Yup.mixed().nullable(),
			}),
			lovvalg: Yup.string().when('kilde', {
				is: (kilde) => kilde === MEDL_KILDER.SRVGOSYS || kilde === MEDL_KILDER.SRVMELOSYS,
				then: () => requiredString,
				otherwise: () => Yup.mixed().nullable(),
			}),
			kildedokument: Yup.string().when('kilde', {
				is: (kilde) => kilde === MEDL_KILDER.SRVGOSYS || kilde === MEDL_KILDER.SRVMELOSYS,
				then: () => requiredString,
				otherwise: () => Yup.mixed().nullable(),
			}),
			studieinformasjon: Yup.object().when('kilde', {
				is: (kilde) => kilde === MEDL_KILDER.LAANEKASSEN,
				then: () =>
					Yup.object({
						statsborgerland: requiredString,
						studieland: requiredString,
						delstudie: requiredBoolean,
						soeknadInnvilget: requiredBoolean,
					}).required(),
				otherwise: () => Yup.mixed().nullable(),
			}),
		}).required()
	),
}

import { ifPresent } from '@/utils/YupValidations'
import * as Yup from 'yup'
import { testDatoTom } from '@/components/fagsystem/utils'

export const tpsMessagingValidation = {
	tpsMessaging: ifPresent(
		'$tpsMessaging',
		Yup.object({
			egenAnsattDatoFom: ifPresent('$tpsMessaging.egenAnsattDatoFom', Yup.string()),
			egenAnsattDatoTom: ifPresent(
				'$tpsMessaging.egenAnsattDatoTom',
				testDatoTom(Yup.string(), 'egenAnsattDatoFom'),
			),
		}),
	),
}

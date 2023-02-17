import * as Yup from 'yup'
import { ifPresent } from '@/utils/YupValidations'
import { testDatoTom } from '@/components/fagsystem/utils'

export const validation = {
	skjerming: ifPresent(
		'$skjerming',
		Yup.object({
			egenAnsattDatoFom: ifPresent('$skjerming.egenAnsattDatoFom', Yup.string()),
			egenAnsattDatoTom: ifPresent(
				'$skjerming.egenAnsattDatoTom',
				testDatoTom(Yup.string(), 'egenAnsattDatoFom')
			),
		})
	),
}

import * as Yup from 'yup'
import { KrrValidation } from '@/components/fagsystem/krrstub/form/KrrValidation'
import { MedlValidation } from '@/components/fagsystem/medl/form/MedlValidation'

export const Validations = Yup.object({
	krrstub: KrrValidation.krrstub,
	medl: MedlValidation.medl,
})

import * as Yup from 'yup'
import { requiredDate, requiredString } from '@/utils/YupValidations'

export const utflytting = Yup.object({
	tilflyttingsland: requiredString,
	tilflyttingsstedIUtlandet: Yup.string().optional().nullable(),
	utflyttingsdato: requiredDate.nullable(),
})

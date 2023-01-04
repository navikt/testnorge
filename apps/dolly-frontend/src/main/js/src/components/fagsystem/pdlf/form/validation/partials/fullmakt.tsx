import * as Yup from 'yup'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import { requiredDate } from '@/utils/YupValidations'
import { nyPerson } from '@/components/fagsystem/pdlf/form/validation/partials/familierelasjoner'

export const fullmakt = Yup.array().of(
	Yup.object({
		omraader: Yup.array().min(1, 'Velg minst ett område'),
		gyldigFraOgMed: testDatoFom(requiredDate, 'gyldigTilOgMed'),
		gyldigTilOgMed: testDatoTom(requiredDate, 'gyldigFraOgMed'),
		motpartsPersonident: Yup.string().nullable(),
		nyFullmektig: nyPerson,
	})
)

import * as Yup from 'yup'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import { requiredDate } from '@/utils/YupValidations'
import { nyPerson } from '@/components/fagsystem/pdlf/form/validation/partials/familierelasjoner'

export const fullmakt = Yup.object({
	omraader: Yup.array().min(1, 'Velg minst ett område'),
	gyldigFraOgMed: testDatoFom(requiredDate.nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(requiredDate.nullable(), 'gyldigFraOgMed'),
	motpartsPersonident: Yup.string()
		.test('feltet-mangler', 'Fullmektig er påkrevd', (value, testcontext) => {
			return value || testcontext.options.context?.identMaster !== 'PDL'
		})
		.nullable(),
	nyFullmektig: nyPerson,
})

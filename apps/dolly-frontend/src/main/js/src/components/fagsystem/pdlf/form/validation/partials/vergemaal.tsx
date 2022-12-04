import * as Yup from 'yup'
import { requiredString } from '~/utils/YupValidations'
import { testDatoFom, testDatoTom } from '~/components/fagsystem/utils'
import { nyPerson } from '~/components/fagsystem/pdlf/form/validation/partials/familierelasjoner'

export const vergemaal = Yup.object({
	vergemaalEmbete: requiredString.nullable(),
	sakType: requiredString.nullable(),
	gyldigFraOgMed: testDatoFom(Yup.mixed().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.mixed().nullable(), 'gyldigFraOgMed'),
	nyVergeIdent: nyPerson,
	vergeIdent: Yup.string().nullable(),
	mandatType: Yup.string().nullable(),
})

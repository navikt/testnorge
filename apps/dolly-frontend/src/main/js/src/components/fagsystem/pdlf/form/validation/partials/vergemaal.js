import * as Yup from 'yup'
import { requiredString } from '~/utils/YupValidations'
import { testDatoFom, testDatoTom } from '~/components/fagsystem/utils'
import { nyPerson } from '~/components/fagsystem/pdlf/form/validation/partials/familierelasjoner'

export const vergemaal = Yup.array().of(
	Yup.object({
		vergemaalEmbete: requiredString.nullable(),
		sakType: requiredString.nullable(),
		gyldigFraOgMed: testDatoFom(Yup.string().nullable(), 'gyldigTilOgMed'),
		gyldigTilOgMed: testDatoTom(Yup.string().nullable(), 'gyldigFraOgMed'),
		nyVergeIdent: nyPerson,
		vergeIdent: Yup.string().nullable(),
		mandatType: Yup.string().nullable(),
	})
)

import * as Yup from 'yup'
import { messages, requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import { nyPerson } from '@/components/fagsystem/pdlf/form/validation/partials/familierelasjoner'

const tjenesteomraade = Yup.object({
	tjenesteoppgave: Yup.array()
		.of(Yup.string())
		.min(1, 'Velg minst én tjenesteoppgave')
		.required(messages.required),
	tjenestevirksomhet: requiredString,
})

export const vergemaal = Yup.object({
	vergemaalEmbete: requiredString,
	sakType: requiredString,
	gyldigFraOgMed: testDatoFom(Yup.mixed().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.mixed().nullable(), 'gyldigFraOgMed'),
	nyVergeIdent: nyPerson,
	vergeIdent: Yup.string().nullable(),
	mandatType: Yup.string().nullable(),
	tjenesteomraade: Yup.array().of(tjenesteomraade),
})

import * as Yup from 'yup'
import { requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

export const statsborgerskap = Yup.object({
	landkode: requiredString.nullable(),
	gyldigFraOgMed: testDatoFom(Yup.mixed().nullable(), 'gyldigTilOgMed'),
	gyldigTilOgMed: testDatoTom(Yup.mixed().nullable(), 'gyldigFraOgMed'),
	bekreftelsesdato: Yup.date().optional().nullable(),
})

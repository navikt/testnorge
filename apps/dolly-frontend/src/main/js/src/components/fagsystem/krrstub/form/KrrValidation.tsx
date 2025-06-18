import * as Yup from 'yup'
import { ifPresent, requiredBoolean, requiredDate, requiredString } from '@/utils/YupValidations'
import * as _ from 'lodash-es'

const testMobil = (val: Yup.StringSchema) => {
	return val.test(
		'gyldig-mobil',
		'Ugyldig telefonnummer',
		(mobil: string | undefined, testContext: Yup.TestContext) => {
			const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
			const registrert = _.get(fullForm, 'krrstub.registrert')
			const landkode = _.get(fullForm, 'krrstub.landkode')
			if (!registrert || !landkode || _.isEmpty(mobil)) {
				return true
			}
			if (landkode === '+47') {
				return (
					(mobil?.length === 8 && mobil?.match('^[0-9]+$')) ||
					testContext.createError({ message: 'Norske telefonnummer må ha 8 sifre' })
				)
			} else {
				return (
					(mobil?.length > 3 && mobil?.length < 12 && mobil.match('^[0-9]+$')) ||
					testContext.createError({ message: 'Telefonnummer må ha mellom 4 og 11 siffer' })
				)
			}
		},
	)
}

export const KrrValidation = {
	krrstub: ifPresent(
		'$krrstub',
		Yup.object({
			epost: Yup.string(),
			gyldigFra: Yup.date().when('registrert', {
				is: true,
				then: () => requiredDate.nullable(),
				otherwise: () => Yup.date().nullable(),
			}),
			landkode: Yup.mixed().when(['registrert', 'mobil'], {
				is: (registrert: boolean, mobil: string) => registrert && mobil,
				then: () => requiredString,
				otherwise: () => Yup.mixed().nullable(),
			}),
			mobil: testMobil(Yup.string().nullable()),
			sdpAdresse: Yup.string(),
			sdpLeverandoer: Yup.string().nullable(),
			spraak: Yup.string(),
			registrert: ifPresent('$krrstub.registrert', requiredBoolean),
			reservert: Yup.boolean().nullable(),
		}),
	),
}

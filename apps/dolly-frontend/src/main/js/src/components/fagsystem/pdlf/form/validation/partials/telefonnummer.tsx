import * as Yup from 'yup'
import { requiredString } from '@/utils/YupValidations'
import * as _ from 'lodash-es'

const testTelefonnummer = () =>
	Yup.string()
		.max(20, 'Telefonnummer kan ikke ha mer enn 20 sifre')
		.when('landskode', {
			is: '+47',
			then: () => Yup.string().length(8, 'Norske telefonnummer må ha 8 sifre'),
		})
		.required('Feltet er påkrevd')
		.matches(/^[1-9]\d*$/, 'Telefonnummer må være numerisk, og kan ikke starte med 0')

const testPrioritet = (val) => {
	return val.test('prioritet', 'Ugyldig prioritet', (val, testContext) => {
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
		if (Object.keys(testContext.parent).length < 1) return true
		const index = testContext.index || 0
		const tlfListe =
			_.get(fullForm, 'pdldata.person.telefonnummer') || _.get(fullForm, 'telefonnummer')
		if (tlfListe?.length < 2) {
			return tlfListe?.[index]?.prioritet === 1
		}
		const index2 = index === 0 ? 1 : 0
		return tlfListe?.[index]?.prioritet !== tlfListe?.[index2]?.prioritet
	})
}

export const telefonnummer = Yup.object({
	landskode: requiredString,
	nummer: testTelefonnummer(),
	prioritet: testPrioritet(Yup.mixed().required()),
}).nullable()

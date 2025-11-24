import * as _ from 'lodash-es'
import * as Yup from 'yup'
import { requiredDate, requiredString } from '@/utils/YupValidations'

const validInnflyttingsdato = (val) => {
	return val.test('gyldig-innflyttingsdato', (value, testContext) => {
		if (!value) return true
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
		const innflyttingsdato = value
		const utflyttingsdato = _.get(fullForm, 'pdldata.person.utflytting[0].utflyttingsdato')
		if (
			!_.isNil(utflyttingsdato) &&
			new Date(innflyttingsdato).getDate() === new Date(utflyttingsdato).getDate()
		) {
			return testContext.createError({
				message: 'Innflyttingsdato kan ikke være lik utflyttingsdato',
			})
		}
		return true
	})
}

export const innflytting = Yup.object({
	fraflyttingsland: requiredString,
	fraflyttingsstedIUtlandet: Yup.string().optional().nullable(),
	innflyttingsdato: validInnflyttingsdato(requiredDate.nullable()),
})

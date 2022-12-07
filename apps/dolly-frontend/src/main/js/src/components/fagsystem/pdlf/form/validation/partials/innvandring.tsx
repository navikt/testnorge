import * as _ from 'lodash-es'
import * as Yup from 'yup'
import { requiredDate, requiredString } from '@/utils/YupValidations'

const validInnflyttingsdato = (val) => {
	return val.test('gyldig-innflyttingsdato', function isWithinTest(value) {
		if (!value) return true
		const innflyttingsdato = value
		const values = this.options.context
		const utflyttingsdato = _.get(values, 'pdldata.person.utflytting[0].utflyttingsdato')
		if (
			!_.isNil(utflyttingsdato) &&
			new Date(innflyttingsdato).getDate() === new Date(utflyttingsdato).getDate()
		) {
			return this.createError({ message: 'Innflyttingsdato kan ikke være lik utflyttingsdato' })
		}
		return true
	})
}

export const innflytting = Yup.object({
	fraflyttingsland: requiredString,
	fraflyttingsstedIUtlandet: Yup.string().optional().nullable(),
	innflyttingsdato: validInnflyttingsdato(requiredDate),
})

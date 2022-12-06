import _get from 'lodash/get'
import { isNil } from 'lodash'
import * as Yup from 'yup'
import { requiredDate, requiredString } from '@/utils/YupValidations'

const validInnflyttingsdato = (val) => {
	return val.test('gyldig-innflyttingsdato', function isWithinTest(value) {
		if (!value) return true
		const innflyttingsdato = value
		const values = this.options.context
		const utflyttingsdato = _get(values, 'pdldata.person.utflytting[0].utflyttingsdato')
		if (
			!isNil(utflyttingsdato) &&
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

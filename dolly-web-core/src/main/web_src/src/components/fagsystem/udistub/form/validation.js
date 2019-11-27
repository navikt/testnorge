import * as Yup from 'yup'
import { requiredDate, ifPresent, requiredString, requiredBoolean } from '~/utils/YupValidations'

const aliaser = Yup.array().of(
	Yup.object({
		nyIdent: requiredBoolean,
		identtype: Yup.string().when('nyIdent', {
			is: true,
			then: requiredString
		})
	})
)

const arbeidsadgang = Yup.object({
	arbeidsOmfang: Yup.string(),
	harArbeidsAdgang: requiredString,
	periode: {
		fra: requiredDate,
		til: requiredDate
	},
	typeArbeidsadgang: Yup.string()
})

export const validation = Yup.object({
	aliaser: ifPresent('$udistub.aliaser', aliaser),
	arbeidsadgang: ifPresent('$udistub.arbeidsadgang', arbeidsadgang),
	flyktning: ifPresent('$udistub.flyktning', requiredBoolean),
	oppholdStatus: Yup.object({}).nullable(),
	soeknadOmBeskyttelseUnderBehandling: ifPresent(
		'$udistub.soeknadOmBeskyttelseUnderBehandling',
		requiredString
	)
})

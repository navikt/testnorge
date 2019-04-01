import * as Yup from 'yup'

export const defaultDateFormat = 'dd.MM.yyyy'

const schema = required => {
	return Yup.string().test('testDate', 'Formatet må være DD.MM.YYYY.', val => {
		if (val.length !== 0) {
			const regex = /^(?:(?:31(\.)(?:0?[13578]|1[02]))\1|(?:(?:29|30)(\.)(?:0[1,3-9]|1[0-2])\2))(?:(?:1[6-9]|[2-9]\d)\d{2})$|^(?:29(\.)02\3(?:(?:(?:1[6-9]|[2-9]\d)(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0[1-9]|1\d|2[0-8])(\.)(?:(?:0[1-9])|(?:1[0-2]))\4(?:(?:1[6-9]|[2-9]\d)\d{2})$/
			return regex.test(val)
		}

		if (val.length === 0 && required === false) return true
	})
}

export default schema

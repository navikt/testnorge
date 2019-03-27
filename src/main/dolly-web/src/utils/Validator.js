import * as yup from 'yup'

// Workaround for integer-validator bug av yup
class Int extends yup.number {
	_typeCheck(value) {
		return Number.isInteger(value)
	}
}

export const NumberValidator = new Int()

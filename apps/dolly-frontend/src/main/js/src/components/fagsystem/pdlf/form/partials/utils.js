import _get from 'lodash/get'
import _isNil from 'lodash/isNil'

export const getPlaceholder = (values, selectedValuePath) => {
	const fornavn = _get(values, `${selectedValuePath}.fornavn`)
	let mellomnavn = _get(values, `${selectedValuePath}.mellomnavn`)
	const etternavn = _get(values, `${selectedValuePath}.etternavn`)

	mellomnavn = mellomnavn !== '' ? ' ' + mellomnavn : mellomnavn
	return !_isNil(fornavn) && fornavn !== '' ? fornavn + mellomnavn + ' ' + etternavn : 'Velg..'
}

export const setNavn = (navn, path, setFieldValue) => {
	if (!navn) {
		setFieldValue(path, null)
	} else {
		const deltNavn = navn.label.split(' ')
		setFieldValue(path, {
			fornavn: navn.value,
			mellomnavn: deltNavn.length === 3 ? deltNavn[1] : '',
			etternavn: deltNavn[deltNavn.length - 1],
		})
	}
}

export const setValue = (value, path, setFieldValue) => {
	setFieldValue(`${path}`, value.value)
}

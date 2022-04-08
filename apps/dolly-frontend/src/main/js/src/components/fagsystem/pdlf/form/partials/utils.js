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

export const isEmpty = (attributt) => {
	const flattenData = (objekt) => {
		let result = {}
		for (const i in objekt) {
			if (typeof objekt[i] === 'object' && !Array.isArray(objekt[i])) {
				const temp = flattenData(objekt[i])
				for (const j in temp) {
					result[i + '.' + j] = temp[j]
				}
			} else {
				result[i] = objekt[i]
			}
		}
		return result
	}

	return (
		attributt?.empty ||
		Object.values(flattenData(attributt)).every((x) => x === null || x === '' || x === false)
	)
}

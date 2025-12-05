import * as _ from 'lodash-es'

export const getPlaceholder = (values, selectedValuePath) => {
	const fornavn = _.get(values, `${selectedValuePath}.fornavn`)
	let mellomnavn = _.get(values, `${selectedValuePath}.mellomnavn`)
	const etternavn = _.get(values, `${selectedValuePath}.etternavn`)

	mellomnavn = mellomnavn !== '' ? ' ' + mellomnavn : mellomnavn
	return !_.isNil(fornavn) && fornavn !== '' ? fornavn + mellomnavn + ' ' + etternavn : 'Velg ...'
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

export const isEmpty = (attributt: any, excludeList = [] as Array<string>) => {
	const flattenData = (objekt) => {
		let result = {}
		for (const i in objekt) {
			if (excludeList.includes(i)) {
				continue
			}
			if (
				typeof objekt[i] === 'object' &&
				!(objekt[i] instanceof Date) &&
				!Array.isArray(objekt[i])
			) {
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
		Object.values(flattenData(attributt)).every(
			(x) => x === null || x === '' || x === false || x === undefined || x?.length === 0,
		)
	)
}

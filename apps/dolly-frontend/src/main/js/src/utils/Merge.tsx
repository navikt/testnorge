import * as _ from 'lodash-es'

function _mergeKeepShapeArray(dest, source) {
	if (source.length != dest.length) {
		return dest
	}
	const ret = []
	dest.forEach((v, i) => {
		ret[i] = _mergeKeepShape(v, source[i])
	})
	return ret
}

function _mergeKeepShapeObject(dest, source) {
	const ret = {}
	Object.keys(dest).forEach((key) => {
		const sourceValue = source[key]
		if (typeof sourceValue !== 'undefined') {
			ret[key] = _mergeKeepShape(dest[key], sourceValue)
		} else {
			ret[key] = dest[key]
		}
	})
	return ret
}

function _mergeKeepShape(dest, source) {
	// else if order matters here, because _.isObject is true for arrays also
	if (_.isDate(dest)) {
		if (!_.isDate(source)) {
			return dest
		}
		return source
	} else if (_.isArray(dest)) {
		if (!_.isArray(source)) {
			return dest
		}
		return _mergeKeepShapeArray(dest, source)
	} else if (_.isObject(dest)) {
		if (!_.isObject(source)) {
			return dest
		}
		return _mergeKeepShapeObject(dest, source)
	} else {
		return source
	}
}

/**
 * Immutable merge that retains the shape of the `existingValue`
 */
export const mergeKeepShape = (existingValue, extendingValue) => {
	return _mergeKeepShape(existingValue, extendingValue)
}

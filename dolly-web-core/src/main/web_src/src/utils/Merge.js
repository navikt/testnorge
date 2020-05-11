import _isArray from 'lodash/isArray'
import _isObject from 'lodash/isObject'
import _isDate from 'lodash/isDate'

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
	Object.keys(dest).forEach(key => {
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
	if (_isDate(dest)) {
		if (!_isDate(source)) {
			return dest
		}
		return source
	} else if (_isArray(dest)) {
		if (!_isArray(source)) {
			return dest
		}
		return _mergeKeepShapeArray(dest, source)
	} else if (_isObject(dest)) {
		if (!_isObject(source)) {
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

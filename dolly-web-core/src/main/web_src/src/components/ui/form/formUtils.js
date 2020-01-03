import _has from 'lodash/has'
import _isNil from 'lodash/isNil'

export const fieldError = meta => {
	return !!meta.touched && !!meta.error ? { feilmelding: meta.error } : null
}

export const panelError = (formikBag, attributtPath) => {
	// Ignore if values ikke er satt
	if (_isNil(attributtPath)) return false

	// Strings er akseptert, men konverter til Array
	if (!Array.isArray(attributtPath)) attributtPath = [attributtPath]

	return attributtPath.some(attr => _has(formikBag.errors, attr))
}

export const SyntEvent = (name, value) => ({ target: { name, value } })

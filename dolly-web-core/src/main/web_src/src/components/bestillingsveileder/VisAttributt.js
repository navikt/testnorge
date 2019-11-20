import { useFormikContext } from 'formik'
import _isNil from 'lodash/isNil'
import _get from 'lodash/get'
export { pathAttrs } from '~/service/attributter/Attributter'

export const Vis = ({ attributt, children }) => {
	const isChecked = (initial, attributtPath) => {
		// Ignore if values ikke er satt
		if (_isNil(attributtPath)) return false

		// Strings er akseptert, men konverter til Array
		if (!_isNil(attributtPath) && !Array.isArray(attributtPath)) attributtPath = [attributtPath]

		return attributtPath.some(v => Boolean(_get(initial, v)))
	}

	const { initialValues } = useFormikContext()
	return isChecked(initialValues, attributt) && children
}

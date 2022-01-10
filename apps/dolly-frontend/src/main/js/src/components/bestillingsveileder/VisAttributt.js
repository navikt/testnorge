import { connect } from 'formik'
import _isNil from 'lodash/isNil'
import _has from 'lodash/has'

const InternalVis = ({ formik = null, attributt, children }) => {
	const isChecked = (values, attributtPath) => {
		// Ignore if values ikke er satt
		if (_isNil(attributtPath)) return false

		// Strings er akseptert, men konverter til Array
		if (!Array.isArray(attributtPath)) attributtPath = [attributtPath]

		return attributtPath.some((v) => _has(values, v))
	}

	return isChecked(formik.values, attributt) && children
}

export const Vis = connect(InternalVis)

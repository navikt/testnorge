import { connect } from 'formik'
import * as _ from 'lodash-es'

const InternalVis = ({ formik = null, attributt, children }) => {
	const isChecked = (values, attributtPath) => {
		// Ignore if values ikke er satt
		if (_.isNil(attributtPath)) return false

		// Strings er akseptert, men konverter til Array
		if (!Array.isArray(attributtPath)) attributtPath = [attributtPath]

		return attributtPath.some((v) => _.has(values, v))
	}

	return isChecked(formik?.values, attributt) && children
}

export const Vis = connect(InternalVis)

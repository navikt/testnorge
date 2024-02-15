import _ from 'lodash'
import { useFormContext } from 'react-hook-form'

export const Vis = ({ attributt, children }) => {
	const { getValues } = useFormContext()
	const isChecked = (values, attributtPath) => {
		// Ignore if values ikke er satt
		if (_.isNil(attributtPath)) return false

		// Strings er akseptert, men konverter til Array
		if (!Array.isArray(attributtPath)) attributtPath = [attributtPath]

		return attributtPath.some((v) => _.has(values, v))
	}
	return isChecked(getValues(), attributt) && children
}

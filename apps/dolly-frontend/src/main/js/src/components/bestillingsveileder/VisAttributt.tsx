import * as _ from 'lodash-es'
import { useFormContext } from 'react-hook-form'

export const Vis = ({ attributt, children }) => {
	const { getValues } = useFormContext()
	const isChecked = (values, attributtPath) => {
		// Ignorer hvis values ikke er satt
		if (_.isNil(attributtPath)) return false

		// Gjør om string til array hvis feil type
		if (!Array.isArray(attributtPath)) {
			attributtPath = [attributtPath]
		}

		return attributtPath.some((v) => _.has(values, v))
	}
	return isChecked(getValues(), attributt) && children
}

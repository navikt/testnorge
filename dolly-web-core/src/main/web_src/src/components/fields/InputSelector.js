import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikDatepicker } from '~/components/fields/Datepicker/Datepicker'
import { FormikInput } from '~/components/fields/Input/Input'

export default function InputSelector(inputType) {
	switch (inputType) {
		case 'date':
			return FormikDatepicker
		case 'select':
			return FormikDollySelect
		case 'number':
		case 'string':
		default:
			return FormikInput
	}
}

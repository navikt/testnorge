import InputSelector from '../InputSelector'

import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikDatepicker } from '~/components/fields/Datepicker/Datepicker'
import { FormikInput } from '~/components/fields/Input/Input'

describe('InputSelector.js', () => {
	it('should return correct component', () => {
		expect(InputSelector('date')).toEqual(FormikDatepicker)
		expect(InputSelector('select')).toEqual(FormikDollySelect)
		expect(InputSelector('number')).toEqual(FormikInput)
		expect(InputSelector('string')).toEqual(FormikInput)
		expect(InputSelector()).toEqual(FormikInput)
	})
})

import React from 'react'
import { shallow } from 'enzyme'
import Select from '../Select/Select'

describe('Select.js', () => {
	it('should render select', () => {
		const wrapper = shallow(<Select className="test" name="test" label="test" />)

		expect(wrapper.find('.test')).toBeTruthy()
	})
})

import React from 'react'
import { shallow } from 'enzyme'
import Checkbox from '../Checkbox/Checkbox'

describe('Checkbox.js', () => {
	it('should render a checkbox', () => {
		const wrapper = shallow(<Checkbox id="test" label="test" />)

		expect(wrapper.find('.dolly-checkbox').exists()).toBeTruthy()
	})
})

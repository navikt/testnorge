import React from 'react'
import { shallow } from 'enzyme'
import Input from '../Input/Input'

describe('Input.js', () => {
	it('should render input', () => {
		const wrapper = shallow(<Input className="test" label="test" />)

		expect(wrapper.find('.test')).toBeTruthy()
	})
})

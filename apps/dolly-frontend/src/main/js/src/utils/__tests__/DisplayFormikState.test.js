import React from 'react'
import { shallow } from 'enzyme'
import DisplayFormikState from '../DisplayFormikState'

describe('DisplayFormikState.js', () => {
	const wrapper = shallow(<DisplayFormikState />)

	it('should render pre', () => {
		expect(wrapper.find('pre').exists()).toBeTruthy()
	})

	it('it should render strong', () => {
		expect(wrapper.find('strong').text()).toBe('props')
	})
})

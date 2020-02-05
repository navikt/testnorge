import React from 'react'
import { shallow } from 'enzyme'
import Header from '../Header'

describe('Header.js', () => {
	const testdata = { brukerId: 'test' }
	const wrapper = shallow(<Header brukerData={testdata} />)

	it('should render Header', () => {
		expect(wrapper.find('.app-header').exists()).toBeTruthy()
	})

	it('should render four menu links', () => {
		expect(wrapper.find('.menu-links').children()).toHaveLength(4)
	})

	it('should render a home button', () => {
		expect(wrapper.find('.home-nav').exists()).toBeTruthy()
	})
})

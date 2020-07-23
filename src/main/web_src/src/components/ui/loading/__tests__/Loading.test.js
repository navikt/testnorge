import React from 'react'
import { shallow } from 'enzyme'
import Loading from '../Loading'

describe('Loading component', () => {
	it('should render', () => {
		const wrapper = shallow(<Loading />)
		expect(wrapper.text()).toBe('Laster')
		expect(wrapper.children().length).toBe(2)
	})

	it('should only render spinner with flag onlySpinner', () => {
		const wrapper = shallow(<Loading onlySpinner />)
		expect(wrapper.children().length).toBe(0)
		expect(wrapper.hasClass('loading-spinner')).toBe(true)
	})

	it('should be able to change label', () => {
		const mytestlabel = 'mytestlabel'
		const wrapper = shallow(<Loading label={mytestlabel} />)
		expect(wrapper.text()).toBe(mytestlabel)
	})

	it('should render fullpage container', () => {
		const wrapper = shallow(<Loading fullpage />)
		expect(wrapper.hasClass('fullpage-loading-container')).toBe(true)
		expect(wrapper.children().length).toBe(1)
	})

	it('should render panel container', () => {
		const wrapper = shallow(<Loading panel />)
		expect(wrapper.hasClass('panel-loading-container')).toBe(true)
		expect(wrapper.children().length).toBe(1)
	})
})

import React from 'react'
import { shallow } from 'enzyme'
import NavButton from '../NavButton/NavButton'

describe('NavButton.tsx', () => {
	it('should render forward navbutton', () => {
		const wrapper = shallow(<NavButton direction="forward" />)
		expect(wrapper.find('.nav-button-container').exists()).toBeTruthy()
	})
	it('should render backward navbutton', () => {
		const wrapper = shallow(<NavButton direction="backward" />)
		expect(wrapper.find('.nav-button-container').exists()).toBeTruthy()
	})
})

import React from 'react'
import { shallow } from 'enzyme'
import Toolbar from '../Toolbar'

import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'

describe('Toolbar.js', () => {
	const testheader = 'testheader'
	const searchfieldTest = <div className="testsearch" />
	const toggleOnChange = jest.fn()
	const wrapper = shallow(
		<Toolbar title={testheader} searchField={searchfieldTest} toggleOnChange={toggleOnChange} />
	)

	it('should render toolbar', () => {
		expect(wrapper.find('.toolbar').exists()).toBeTruthy()
	})

	it('should render title', () => {
		expect(wrapper.find('h2').text()).toBe(testheader)
	})

	it('should render searchfield', () => {
		expect(wrapper.find('.testsearch').exists()).toBeTruthy()
	})

	it('should render component ToggleGruppe and ToggleKnapp', () => {
		expect(wrapper.find(ToggleGruppe).exists()).toBeTruthy()
		expect(wrapper.find(ToggleKnapp).exists()).toBeTruthy()
	})
})

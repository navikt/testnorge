import React from 'react'
import { shallow } from 'enzyme'
import Panel from '../Panel'

const findHeading = (wrapper) => wrapper.find('.panel-heading').childAt(0).text()

describe('Panel.js', () => {
	it('should render panel with default header ', () => {
		const wrapper = shallow(<Panel />)
		expect(findHeading(wrapper)).toBe('Panel')
	})

	it('should render props', () => {
		const testHeading = 'TestPanelHeader'
		const testContent = 'TestPanelContent'
		const wrapper = shallow(<Panel heading={testHeading} content={testContent} startOpen />)

		expect(findHeading(wrapper)).toBe(testHeading)
		expect(wrapper.find('.panel-content').exists()).toBe(true)
		expect(wrapper.find('.panel-content').text()).toBe(testContent)
	})

	it('should have an extra class when open', () => {
		const wrapper = shallow(<Panel startOpen />)
		expect(wrapper.hasClass('panel-open')).toBe(true)
	})

	it('should render children if specified', () => {
		const childrenContent = 'TestChildrenContent'
		const wrapper = shallow(<Panel startOpen>{childrenContent}</Panel>)
		expect(wrapper.find('.panel-content').text()).toBe(childrenContent)
	})

	it('should render error in panel', () => {
		const wrapper = shallow(<Panel hasErrors />)
		expect(wrapper.find('.panel-heading_error').exists()).toBeTruthy()
	})
})

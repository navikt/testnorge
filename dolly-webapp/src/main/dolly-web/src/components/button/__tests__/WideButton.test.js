import React from 'react'
import { shallow } from 'enzyme'
import WideButton from '../WideButton/WideButton'

describe('WideButton.js', () => {
	const testText = 'test'
	const wrapper = shallow(<WideButton iconKind="add" text={testText} />)
	it('should render a wide button', () => {
		expect(wrapper.find('.wide-button').exists()).toBeTruthy()
	})

	it('should render a wide button with text', () => {
		expect(wrapper.find('.wide-button_label_text').text()).toBe(testText)
	})
})

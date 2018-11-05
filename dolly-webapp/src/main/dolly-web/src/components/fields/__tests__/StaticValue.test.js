import React from 'react'
import { shallow } from 'enzyme'
import StaticValue from '../StaticValue/StaticValue'

describe('StaticValue.js', () => {
	it('should render static value with default headerType', () => {
		const testValue = 'test'
		const wrapper = shallow(<StaticValue header={testValue} value={testValue} />)
		expect(wrapper.find('h2').exists()).toBeTruthy()
		expect(wrapper.find('h2').text()).toBe(testValue)
		expect(wrapper.find('span').text()).toBe(testValue)
	})
	it('should render staticvalue with formatted value', () => {
		const testFormatter = text => {
			return text + '1'
		}

		const testValue = 'test'
		const res = 'test1'
		const wrapper = shallow(
			<StaticValue header={testValue} value={testValue} format={testFormatter} />
		)
		expect(wrapper.find('span').text()).toBe(res)
	})
})

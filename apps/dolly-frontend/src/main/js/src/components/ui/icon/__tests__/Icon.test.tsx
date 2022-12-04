import React from 'react'
import { shallow } from 'enzyme'
import Icon, { Chevron, iconList } from '../Icon'

describe('Icon.js', () => {
	it('should return a svg arrow with size', () => {
		const wrapper = shallow(<Icon kind="arrow-circle-right" size="16" />)

		expect(wrapper.find('.svg-arrow-circle-right').exists()).toBeTruthy()
	})

	it('should return null when kind is undefined', () => {
		const wrapper = shallow(<Icon />)

		expect(wrapper.children()).toHaveLength(0)
	})

	iconList.map((icon) => {
		it(`it should return a svg: ${icon}`, () => {
			const wrapper = shallow(<Icon kind={icon} />)

			expect(wrapper.find('svg').exists()).toBeTruthy()
		})
	})
})

describe('Chevron.js', () => {
	const wrapper = shallow(<Chevron />)

	expect(wrapper.find('g').exists()).toBeTruthy()
})

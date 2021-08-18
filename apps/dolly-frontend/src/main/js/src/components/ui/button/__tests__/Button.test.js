import React from 'react'
import { shallow } from 'enzyme'
import Button from '../Button'

describe('Button.js', () => {
	const onClickHandler = jest.fn()
	const stopPropagation = jest.fn()
	it('should render Button', () => {
		const wrapper = shallow(<Button />)

		expect(wrapper.find('button').exists()).toBeTruthy()
	})

	it('should handle onClick', () => {
		const wrapper = shallow(<Button onClick={onClickHandler} />)

		wrapper.find('button').simulate('click', { stopPropagation: stopPropagation })
		expect(onClickHandler).toBeCalled()
	})

	it('should handle button with children', () => {
		const children = 'test'
		const wrapper = shallow(<Button>{children}</Button>)

		expect(wrapper.find('button').text()).toBe(children)
	})

	it('should render a button with some icon', () => {
		const wrapper = shallow(<Button kind="add" />)
		//since the actual Icon is not rendered due to shallow, we check if it has a child (icon)
		expect(wrapper.children()).toHaveLength(1)
	})
})

import React from 'react'
import { shallow } from 'enzyme'
import LinkButton from '../LinkButton/LinkButton'

describe('LinkButton.js', () => {
	const testText = 'test'
	const preventDefault = jest.fn()
	const onClickHandler = jest.fn()

	const wrapper = shallow(
		<LinkButton text="test" onClick={onClickHandler} preventDefault={false} />
	)

	it('should render a linkbutton with text', () => {
		expect(wrapper.find('a').exists()).toBeTruthy()
		expect(wrapper.find('a').text()).toBe(testText)
	})

	it('should trigger onClick', () => {
		wrapper.find('a').simulate('click')
		expect(onClickHandler).toBeCalled()
	})

	const wrapperWithPreventDefault = shallow(<LinkButton text="test" onClick={onClickHandler} />)

	it('it should trigger onClick and preventDefault', () => {
		wrapperWithPreventDefault.find('a').simulate('click', { preventDefault })
		expect(onClickHandler).toBeCalled()
		expect(preventDefault).toBeCalled()
	})
})

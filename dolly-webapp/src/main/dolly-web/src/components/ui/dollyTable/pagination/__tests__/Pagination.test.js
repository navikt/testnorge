import React from 'react'
import { shallow } from 'enzyme'
import Pagination from '../Pagination'

describe('Pagination.js', () => {
	const items = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]

	const wrapper = shallow(
		<Pagination
			items={items}
			render={cb => {
				return <div className="test-comp">content</div>
			}}
		/>
	)

	it('should render pagination', () => {
		expect(wrapper.find('.pagination-wrapper').exists()).toBeTruthy()
		expect(wrapper.find('.pagination-label').exists()).toBeTruthy()
	})

	it('should render the render prop', () => {
		expect(wrapper.find('.test-comp'))
	})

	it('should render pagination label twice', () => {
		expect(wrapper.find('.pagination-label')).toHaveLength(2)
	})

	const instance = wrapper.instance()

	it('should calculate pageCount', () => {
		expect(instance._calculatePageCount()).toEqual(2)
	})

	it('should calculate items to render', () => {
		const expectedData = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
		expect(instance._calculateItemsToRender()).toEqual(expectedData)
	})

	it('should calculate correct startIndex', () => {
		expect(instance._calculateStartIndex()).toEqual(0)
	})

	it('should handle pageHandler and return new array', () => {
		instance._pageChangeHandler({ selected: 1 })
		expect(wrapper.state().currentPage).toEqual(1)

		const expectedData = [11, 12, 13, 14, 15]
		expect(instance._calculateItemsToRender()).toEqual(expectedData)
	})

	it('should handle componentDidUpdate on new props', () => {
		const newItems = [1, 2, 3, 4, 5, 6]
		wrapper.setState({ currentPage: 1 })
		expect(wrapper.state().currentPage).toEqual(1)

		wrapper.setProps({ search: 'test' })

		expect(wrapper.state().currentPage).toEqual(0)
	})
})

import React from 'react'
import { shallow } from 'enzyme'
import Table from '../Table'

describe('Table component', () => {
	describe('<Table />', () => {
		it('should render a div wrapper with class', () => {
			const testChildren = 'TestChildren'
			const wrapper = shallow(<Table>{testChildren}</Table>)
			expect(wrapper.type()).toBe('div')
			expect(wrapper.hasClass('dot')).toBe(true)
		})

		it('should render children components', () => {
			const testChildren = 'TestChildren'
			const wrapper = shallow(<Table>{testChildren}</Table>)
			expect(wrapper.text()).toBe(testChildren)
		})
	})

	describe('<Table.Header/>', () => {
		it('should render a div wrapper with class', () => {
			const testChildren = 'TestChildren'
			const wrapper = shallow(<Table.Header>{testChildren}</Table.Header>)
			expect(wrapper.type()).toBe('div')
			expect(wrapper.hasClass('dot-header')).toBe(true)
		})

		it('should render children components', () => {
			const testChildren = 'TestChildren'
			const wrapper = shallow(<Table.Header>{testChildren}</Table.Header>)
			expect(wrapper.text()).toBe(testChildren)
		})
	})

	describe('<Table.Row/>', () => {
		it('should render a div wrapper with class', () => {
			const testChildren = 'TestChildren'
			const wrapper = shallow(<Table.Row>{testChildren}</Table.Row>)
			expect(wrapper.type()).toBe('div')
			expect(wrapper.hasClass('dot-body-row')).toBe(true)
		})

		it('should have a columns container', () => {
			const testChildren = 'TestChildren'
			const wrapper = shallow(<Table.Row>{testChildren}</Table.Row>)
			expect(wrapper.find('.dot-body-row-columns')).toHaveLength(1)
		})

		it('should render children (columns) inside columns container', () => {
			const testChildren = 'TestChildren'
			const wrapper = shallow(<Table.Row>{testChildren}</Table.Row>)
			expect(
				wrapper
					.find('.dot-body-row-columns')
					.childAt(0)
					.text()
			).toBe(testChildren)
		})
	})
})

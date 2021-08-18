import React from 'react'
import { shallow } from 'enzyme'
import Select from 'react-select'
import ItemCountSelect from '../ItemCountSelect/ItemCountSelect'

describe('ItemCountSelect', () => {
	const wrapper = shallow(<ItemCountSelect />)

	it('should render a itemcountselect', () => {
		expect(wrapper.find('.pagination-itemcount').exists()).toBeTruthy()
	})

	it('should render a react-select component', () => {
		expect(wrapper.find(Select).exists()).toBeTruthy()
	})
})

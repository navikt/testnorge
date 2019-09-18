import React from 'react'
import { shallow } from 'enzyme'
import ExpandButton from '../ExpandButton'

describe('ExpandButton.js', () => {
	it('should render a expandbutton', () => {
		const wrapper = shallow(<ExpandButton />)
		expect(wrapper).toHaveLength(1)
	})

	it('should render a expandbutton with expand', () => {
		const wrapper = shallow(<ExpandButton expanded />)
		expect(wrapper).toHaveLength(1)
	})
})

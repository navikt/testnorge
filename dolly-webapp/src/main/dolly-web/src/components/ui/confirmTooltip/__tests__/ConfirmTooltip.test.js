import React from 'react'
import { shallow } from 'enzyme'
import ConfirmTooltip from '../ConfirmTooltip'

describe('ConfirmTooltip.js', () => {
	const wrapper = shallow(<ConfirmTooltip />)

	it('should render confirmtooltip', () => {
		expect(wrapper.children()).toHaveLength(1)
	})
})

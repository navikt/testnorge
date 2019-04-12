import React from 'react'
import { shallow } from 'enzyme'
import ContentTooltip from '../ContentTooltip'

describe('ContentTooltip.js', () => {
	it('should render tooltip with correct content', () => {
		const testChildren = 'testdata'
		const wrapper = shallow(<ContentTooltip>{testChildren}</ContentTooltip>)

		expect(wrapper.find('.content-tooltip').exists()).toBeTruthy()
	})
})

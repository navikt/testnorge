import React from 'react'
import { shallow } from 'enzyme'
import Version from '../Version'

describe('Version.js', () => {
	global.BUILD = {}
	global.BUILD.VERSION = 'test'
	global.BUILD.COMMITHASH = 'testhash'
	global.BUILD.BRANCH = 'testbranch'
	const wrapper = shallow(<Version />)

	it('should render version component', () => {
		expect(wrapper.find('.build-version').exists()).toBeTruthy()
	})
})

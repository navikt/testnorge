import React from 'react'
import { shallow } from 'enzyme'
import ContentContainer from '../ContentContainer'

describe('ContentContainer.js', () => {
	it('should render a container with a DIV', () => {
		const testClass = 'testClass'

		const wrapper = shallow(
			<ContentContainer>
				<div className={testClass}>TEST</div>
			</ContentContainer>
		)

		expect(wrapper.find('.content-container').exists()).toBeTruthy()

		expect(wrapper.find(`.${testClass}`).exists).toBeTruthy()
	})
})

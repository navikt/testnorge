import React from 'react'
import { shallow } from 'enzyme'
import RedigerTeam from '../RedigerTeam'

describe('RedigerTeam.js', () => {
	const createTeam = jest.fn()
	const updateTeam = jest.fn()
	const closeOpprettRedigerTeam = jest.fn()
	describe('component should render a edit component', () => {
		const wrapper = shallow(
			<RedigerTeam
				createTeam={createTeam}
				updateTeam={updateTeam}
				closeOpprettRedigerTeam={closeOpprettRedigerTeam}
				teamIsUpdating={false}
			/>
		)
		it('should render', () => {
			expect(wrapper.exists()).toBeTruthy()
		})
	})

	describe('component should render loading', () => {
		const wrapper = shallow(
			<RedigerTeam
				createTeam={createTeam}
				updateTeam={updateTeam}
				closeOpprettRedigerTeam={closeOpprettRedigerTeam}
				teamIsUpdating={true}
			/>
		)
		it('should render loading component when fetching', () => {
			expect(wrapper.find('.loading').exists()).toBeTruthy()
		})
		it('should render loading component when fetching', () => {
			expect(wrapper.find('.opprett-tabellrad').exists()).toBeTruthy()
		})
	})
})

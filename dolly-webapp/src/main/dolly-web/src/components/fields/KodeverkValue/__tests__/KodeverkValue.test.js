import React from 'react'
import { shallow } from 'enzyme'
import KodeverkValue from '../KodeverkValue'
import StaticValue from '../../StaticValue/StaticValue'
import Loading from '~/components/loading/Loading'

describe('KodeverkValue.js', () => {
	const fetchKodeverk = jest.fn()
	const kodeverkObject = {
		label: 'test',
		id: 'test',
		apiKodeverkId: 'test'
	}

	describe('KodeverkValue when loading', () => {
		const wrapper = shallow(<KodeverkValue fetchKodeverk={fetchKodeverk} header="test" />)

		it('should call fetchKodeverk onMount', () => {
			expect(fetchKodeverk).toBeCalled()
		})
		it('should render a loading component', () => {
			expect(wrapper.find('.static-value').exists()).toBeTruthy()
			expect(wrapper.find(Loading).exists()).toBeTruthy()
		})
	})

	describe('KodeverkValue with kodeverkObj', () => {
		const wrapper = shallow(
			<KodeverkValue fetchKodeverk={fetchKodeverk} kodeverkObject={kodeverkObject} header="test" />
		)

		it('should render static value', () => {
			expect(wrapper.find(StaticValue).exists()).toBeTruthy()
		})
	})
})

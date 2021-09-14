import React from 'react'
import { shallow } from 'enzyme'
import MiljoVelger from '../MiljoVelger'

describe('MiljoVelger', () => {
	const remove = jest.fn()
	const push = jest.fn()
	const form = { touched: { test: 'feil' }, errors: { test: 'feil' } }
	const arrayHelpers = {
		remove,
		push,
		form,
		name: 'test',
	}
	const header = 'test'
	const testEnv = {
		T: [
			{ id: 't1', label: 'T1' },
			{ id: 't2', label: 'T2' },
			{ id: 't3', label: 'T3' },
		],
	}
	describe('all environments are selected', () => {
		const arrayValues = ['t1', 't2', 't3']
		const wrapper = shallow(
			<MiljoVelger
				arrayValues={arrayValues}
				heading={header}
				arrayHelpers={arrayHelpers}
				environments={testEnv}
			/>
		)

		it('should render miljovelger with header', () => {
			expect(wrapper.find('.miljo-velger').exists()).toBeTruthy()
			expect(wrapper.find('h2').text()).toBe(header)
		})

		const checkboxWrapper = wrapper.find('.miljo-velger_checkboxes')
		it('should render correct amount of checkboxes', () => {
			expect(checkboxWrapper.exists()).toBeTruthy()
			expect(checkboxWrapper.children()).toHaveLength(3)
		})

		it('should trigger in onClick and remove a existing item', () => {
			wrapper.instance().onClickHandler({ target: { id: 't1' } })
			expect(remove).toBeCalled()
		})

		it('should remove all items and remove is triggered three times', () => {
			wrapper.instance().fjernAlle({}, 'T', testEnv['T'])
			expect(remove).toBeCalled()
		})
	})

	describe('no environments are selected', () => {
		const arrayValues = []
		const wrapper = shallow(
			<MiljoVelger
				arrayValues={arrayValues}
				heading={header}
				arrayHelpers={arrayHelpers}
				environments={testEnv}
			/>
		)
		it('should trigger in onClick and add item', () => {
			wrapper.instance().onClickHandler({ target: { id: 't1' } })
			expect(push).toBeCalled()
		})

		it('should add all items', () => {
			wrapper.instance().velgAlle({}, 'T', testEnv['T'])
			expect(push).toBeCalled()
		})
	})

	describe('render error', () => {
		it('should render error', () => {
			const arrayValues = []
			const wrapper = shallow(
				<MiljoVelger
					arrayValues={arrayValues}
					heading={header}
					arrayHelpers={arrayHelpers}
					environments={testEnv}
				/>
			)
			expect(wrapper.find('.miljo-velger_error').exists()).toBeTruthy()
			expect(wrapper.find('.miljo-velger_error').text()).toBe('feil')
		})
	})
})

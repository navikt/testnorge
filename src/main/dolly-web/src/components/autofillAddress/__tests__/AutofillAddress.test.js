// import AutofillAddress from '../AutofillAddress'
// import { shallow } from 'enzyme'
// import React from 'react'

// describe('AutofillAddress.js', () => {
// 	const wrapper = shallow(<AutofillAddress />)
// 	const pnrType = 'pnr'
// 	const knrType = 'knr'

// 	it('should generate correct placeholder for postnummer', () => {
// 		wrapper.setState({ type: pnrType })
// 		const res = 'Velg postnummer...'
// 		const placeholder = wrapper.instance().placeHolderGenerator()
// 		expect(placeholder).toBe(res)
// 	})

// 	it('should generate correct placeholder for kommunenummer', () => {
// 		wrapper.setState({ type: knrType })
// 		const res = 'Velg kommunenummer...'
// 		const placeholder = wrapper.instance().placeHolderGenerator()
// 		expect(placeholder).toBe(res)
// 	})

// 	it('radio buttons should have onChange event', () => {
// 		const radioBtn = wrapper.instance()._renderRadioBtn(true, 'random', 'random', 'Tilfeldig')
// 		expect(radioBtn.props.onChange).toBeTruthy()
// 	})

// 	it('should change state when choosing type', () => {
// 		const testType = 'random'
// 		wrapper.instance().chooseType(testType)
// 		expect(wrapper.instance().state.type).toBe(testType)
// 		expect(wrapper.instance().state.input).toBe('')
// 	})
// })

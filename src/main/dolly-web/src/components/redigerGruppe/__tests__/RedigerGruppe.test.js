import React from 'react'
import { shallow } from 'enzyme'
import RedigerGruppe from '../RedigerGruppe'

describe('RedigerGruppe component', () => {
	describe('<RedigerGruppe />', () => {
		it('should render RedigerGruppe comp', () => {
			const wrapper = shallow(<RedigerGruppe />)
			expect(wrapper.exists()).toBeTruthy()
		})

        it('should render RedigerGruppe comp', () => {
			const wrapper = shallow(<RedigerGruppe />)
			expect(wrapper.exists()).toBeTruthy()
        })
        
		// it('should render searchstring', () => {
		// 	const testValue = 'text'
		// 	const wrapper = shallow(<SearchField searchText={testValue} />)
		// 	expect(wrapper.find('input').props().value).toEqual(testValue)
		// })

		// it('should fire a change on the onChange-event', () => {
		// 	const testValue = 'text'
		// 	const changeHandler = jest.fn()
		// 	const wrapper = shallow(<SearchField searchText={testValue} setSearchText={changeHandler} />)
		// 	wrapper.find('input').simulate('change', { target: { value: testValue } })
		// 	expect(changeHandler).toBeCalledWith(testValue)
		// })
	})
})

import React from 'react'
import { shallow } from 'enzyme'
import RedigerGruppe from '../RedigerGruppe'

describe('RedigerGruppe component', () => {
	describe('<RedigerGruppe />', () => {
		it('should render RedigerGruppe comp', () => {
			const wrapper = shallow(<RedigerGruppe />)
			expect(wrapper.exists()).toBeTruthy()
		})
	})
})

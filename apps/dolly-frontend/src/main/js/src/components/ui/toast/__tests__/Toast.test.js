import React from 'react'
import { shallow } from 'enzyme'
import Toast from '../Toast'
import { ToastContainer } from 'react-toastify'

describe('Toast.tsx', () => {
	const wrapper = shallow(<Toast />)

	it('should render ToastContainer', () => {
		expect(wrapper.find(ToastContainer).exists()).toBeTruthy()
	})
})

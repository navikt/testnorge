import React from 'react'
import { Toast } from './Toast'

describe('<Toast />', () => {
	it('renders', () => {
		cy.mount(<Toast applicationError={'Teste error toast'} />)
	})
})

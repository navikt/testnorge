import React from 'react'
import { AppError } from './AppError'

describe('<AppError />', () => {
	it('renders', () => {
		cy.mount(
			<AppError error={'cypress testing av error'} stackTrace={'cypress testing av stacktrace'} />,
		)
	})
})

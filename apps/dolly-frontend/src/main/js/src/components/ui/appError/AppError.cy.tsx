import React from 'react'
import { AppError } from './AppError'

describe('<AppError />', () => {
	it('renders', () => {
		cy.mount(
			<AppError error={'cypress testing av error'} stackTrace={'cypress testing av stacktrace'} />,
		)
	})
})

describe('<AppError />', () => {
	it('renders', () => {
		cy.mount(
			<AppError
				error={'Failed to fetch dynamically imported module'}
				stackTrace={'Dynamic import feilet'}
			/>,
		)
	})
})

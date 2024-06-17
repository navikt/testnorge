import React from 'react'
import { AppError } from './AppError'

describe('<AppError />', () => {
	it('renders', () => {
		cy.mount(
			<AppError
				error={'playwright testing av error'}
				stackTrace={'playwright testing av stacktrace'}
			/>,
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

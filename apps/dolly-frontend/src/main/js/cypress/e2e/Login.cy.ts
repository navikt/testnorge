/// <reference types="cypress" />

describe('Login side testing', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/login')

		cy.get('button').contains('NAV').click()
	})
})

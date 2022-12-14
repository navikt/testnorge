/// <reference types="cypress" />

describe('Minside testing', () => {
	it('passes', () => {
		cy.visit('http://localhost:3000/minside')

		cy.get('input').type('mal')
	})
})

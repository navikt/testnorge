/// <reference types="cypress" />

describe('Minside testing', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/minside')

		cy.get('input').type('mal')
	})
})

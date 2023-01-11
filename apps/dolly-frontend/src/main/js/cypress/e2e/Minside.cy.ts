/// <reference types="cypress" />

describe('Minside mal testing', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/minside')

		cy.get('input').type('mal')
		cy.get('input').clear()
		cy.get('svg.svg-icon-trashcan').click()
	})
})

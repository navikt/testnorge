/// <reference types="cypress" />

describe('Gruppe testing', () => {
	it('passes', () => {
		cy.visit('http://localhost:3000/gruppe')

		cy.get('button').contains('Ny').click()
		cy.get('input[id=navn]').type('Testing med Cypress')
		cy.get('input[id=hensikt]').type('Masse testing med Cypress')
		cy.get('button').contains('Opprett').click()
	})
})

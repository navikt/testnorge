/// <reference types="cypress" />

describe('Minside mal testing', () => {
	it('passes', () => {
		cy.intercept('DELETE', '*', cy.spy().as('slett_mal'))

		cy.visit('http://localhost:5678/minside')

		cy.get('[data-cy="input_minside_mal"]').type('mal')
		cy.get('[data-cy="input_minside_mal"]').clear()
		cy.get('[data-cy="button_maler_slett"]').click()

		cy.get('@slett_mal').should('have.been.calledOnce')
	})
})

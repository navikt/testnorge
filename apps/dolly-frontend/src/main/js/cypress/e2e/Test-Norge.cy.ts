/// <reference types="cypress" />

describe('Test-Norge søk testing', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/testnorge')

		cy.get('[data-cy="input_testnorge_fnr"]').type('123456')
		cy.get('[data-cy="title_testnorge"]').invoke('show').click()

		cy.get('.skjemaelement__feilmelding').should('contain', 'et tall med')

		cy.get('[data-cy="input_testnorge_fnr"]').clear()
	})
})

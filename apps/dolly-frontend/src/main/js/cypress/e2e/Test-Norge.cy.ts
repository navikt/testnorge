/// <reference types="cypress" />

describe('Test-Norge søk testing', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/testnorge')

		cy.get('[label="Fødselsnummer eller D-dummer"]').type('Test')
		cy.get('[label="Fødselsnummer eller D-dummer"]').clear()

		cy.wait(2000)
	})
})

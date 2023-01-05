/// <reference types="cypress" />

describe('Opprett gruppe og start bestilling med alle mulige tilvalg', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/gruppe')

		cy.get('button').contains('Ny').click()
		cy.get('input[id=navn]').type('Testing med Cypress')
		cy.get('input[id=hensikt]').type('Masse testing med Cypress')
		cy.get('button').contains('Opprett').click()
		cy.url().should('include', '/gruppe/2') // => true
		cy.get('button').contains('Opprett').click()
		cy.get('button').contains('Start').click()
		cy.get('a.dolly-link-button').each((btn) => {
			if (btn.text().includes('Velg alle')) {
				cy.wrap(btn).click()
			}
		})
		cy.get('button').contains('Videre').click()
	})
})

describe('Åpne bestilt ident med knytning mot alle fagsystem', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/gruppe')

		cy.get('div').contains('Testytest').click()
		cy.get('div').contains('12345678912').click()
		cy.wait(1500)

		cy.get('div').contains('Q1').click()
		cy.wait(2000)
	})
})

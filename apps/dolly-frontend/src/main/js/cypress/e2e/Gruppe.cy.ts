/// <reference types="cypress" />

describe('Navigering, Opprett gruppe og start bestilling med alle mulige tilvalg', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/gruppe')

		// Naviger mellom tabs
		cy.get('[data-cy="toggle_favoritter"]').click()
		cy.get('[data-cy="toggle_alle"]').click()
		cy.get('[data-cy="toggle_mine"]').click()

		// Opprett ny gruppe
		cy.get('[data-cy="button_ny_gruppe"]').click()
		cy.get('[data-cy="input_navn"]').type('Testing med Cypress')
		cy.get('[data-cy="input_hensikt"]').type('Masse testing med Cypress')
		cy.get('[data-cy="button_opprett"]').click()
		cy.get('[data-cy="button_opprett_personer"]').click()
		cy.get('[data-cy="toggle_eksisterende_person"]').click()
		cy.get('[data-cy="toggle_ny_person"]').click()
		cy.get('[data-cy="toggle_mal"]').click()
		cy.url().should('include', '/gruppe/2') // => true
		cy.get('[data-cy="button_start_bestilling"]').click()
		cy.get('[data-cy="button_velg_alle"]').each((btn) => cy.wrap(btn).click())
		cy.get('[data-cy="button_videre"]').click()
	})
})

describe('Åpne bestilt ident med knytning mot alle fagsystem', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/gruppe')

		cy.get('div').contains('Testytest').click()
		cy.get('div').contains('12345678912').click()
		cy.wait(3000)

		cy.get('[data-cy="miljoe_hover"]').each((element) => {
			cy.wrap(element).invoke('show').click()
			cy.wait(1000)
			cy.get('[data-cy="title_visning"]').invoke('show').click()
		})
	})
})

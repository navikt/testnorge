/// <reference types="cypress" />

describe('Åpne en organisasjon med alle tilvalg', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/organisasjoner')

		cy.get('div').contains('Logaritme').click()
		cy.get('div').contains('Horisontal').click()
		cy.get('div').contains('Q2').click()
	})
})

describe('Naviger til organisasjoner og start en bestilling med alle tilvalg', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/organisasjoner')

		cy.get('[data-cy="button_opprett_organisasjon"]').click()
		cy.get('[data-cy="button_start_bestilling"]').click()
		cy.get('[data-cy="button_velg_alle"]').each((btn) => cy.wrap(btn).click())
		cy.get('[data-cy="button_videre"]').click()
	})
})

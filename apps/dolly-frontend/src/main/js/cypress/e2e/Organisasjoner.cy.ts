/// <reference types="cypress" />

describe('Åpne en organisasjon med alle tilvalg', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/organisasjoner')

		cy.get('div').contains('Logaritme').click()
		cy.get('div').contains('Horisontal').click()
		cy.get('div').contains('Q2').click()
		cy.wait(2000)
	})
})

describe('Naviger til organisasjoner og start en bestilling med alle tilvalg', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/organisasjoner')

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

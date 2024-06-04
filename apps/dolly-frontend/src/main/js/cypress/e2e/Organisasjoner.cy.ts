import { CypressSelector } from '../mocks/Selectors'

describe('Åpne en organisasjon med alle tilvalg', () => {
	it('passes', () => {
		cy.visit('')

		cy.dollyGet(CypressSelector.BUTTON_HEADER_ORGANISASJONER).click()
		cy.dollyGet(CypressSelector.BUTTON_HEADER_OPPRETT_ORGANISASJONER).click()

		cy.get('div').contains('Logaritme').click()
		cy.get('div').contains('Horisontal').click()
		cy.get('div').contains('Q2').click()
	})
})

describe('Naviger til organisasjoner og start en bestilling med alle tilvalg', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/organisasjoner')

		cy.dollyGet(CypressSelector.BUTTON_HEADER_ORGANISASJONER).click()
		cy.dollyGet(CypressSelector.BUTTON_OPPRETT_ORGANISASJON).click()
		cy.dollyGet(CypressSelector.BUTTON_START_BESTILLING).click()
		cy.dollyGet(CypressSelector.BUTTON_VELG_ALLE).each((btn) => cy.wrap(btn).click())
		cy.dollyGet(CypressSelector.BUTTON_VIDERE).click()
	})
})

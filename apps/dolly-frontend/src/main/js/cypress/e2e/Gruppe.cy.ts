import { CypressSelector } from '../mocks/Selectors'

describe('Navigering, Opprett gruppe og start bestilling med alle mulige tilvalg', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/gruppe')

		// Naviger mellom tabs
		cy.dollyGet(CypressSelector.INPUT_PERSON_SOEK).click({ force: true })
		cy.get('body').type('12345')

		cy.dollyGet(CypressSelector.BUTTON_NAVIGER_PERSON).click()

		cy.wait(400)

		cy.url().should('include', '/gruppe/1')

		cy.dollyGet(CypressSelector.BUTTON_HEADER_PERSONER).click()

		cy.dollyGet(CypressSelector.TOGGLE_FAVORITTER).click()
		cy.dollyGet(CypressSelector.TOGGLE_ALLE).click()
		cy.dollyGet(CypressSelector.TOGGLE_MINE).click()

		// Opprett ny gruppe
		cy.dollyGet(CypressSelector.BUTTON_NY_GRUPPE).click()
		cy.dollyGet(CypressSelector.INPUT_NAVN).type('Testing med Cypress')
		cy.dollyGet(CypressSelector.INPUT_HENSIKT).type('Masse testing med Cypress')
		cy.dollyGet(CypressSelector.BUTTON_OPPRETT).click()
		cy.dollyGet(CypressSelector.BUTTON_OPPRETT_PERSONER).click()
		cy.dollyGet(CypressSelector.TOGGLE_EKSISTERENDE_PERSON).click()
		cy.dollyGet(CypressSelector.TOGGLE_NY_PERSON).click()
		cy.dollyGet(CypressSelector.TOGGLE_MAL).click()

		cy.url().should('include', '/gruppe/2')

		cy.dollyGet(CypressSelector.BUTTON_START_BESTILLING).click()
		cy.dollyGet(CypressSelector.BUTTON_VELG_ALLE).each((btn) => cy.wrap(btn).click())
		cy.dollyGet(CypressSelector.BUTTON_VIDERE).click()
	})
})

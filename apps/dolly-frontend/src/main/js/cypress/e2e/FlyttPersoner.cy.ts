import { CypressSelector } from '../mocks/Selectors'

describe('Dolly flyttPersoner testing', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/gruppe')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.BUTTON_FLYTT_PERSONER).click()

		cy.dollyGet(CypressSelector.TOGGLE_ALLE_GRUPPER).click()
		cy.dollyGet(CypressSelector.TOGGLE_EKSISTERENDE_GRUPPE).click()
		cy.dollyGet(CypressSelector.TOGGLE_NY_GRUPPE).click()

		cy.dollyGet(CypressSelector.INPUT_NY_GRUPPE_NAVN).type('TestNavn')
		cy.dollyGet(CypressSelector.INPUT_NY_GRUPPE_HENSIKT).type('TestHensikt')
		cy.dollyGet(CypressSelector.BUTTON_NY_GRUPPE_OPPRETT).click()

		cy.get('.navds-checkbox__label').contains('12345678912').click()

		cy.dollyGet(CypressSelector.CONTAINER_VALGTE_PERSONER).should('contain', '12345678912')
		cy.dollyGet(CypressSelector.BUTTON_FLYTT_PERSONER_NULLSTILL).click()
		cy.dollyGet(CypressSelector.CONTAINER_VALGTE_PERSONER).should('not.contain', '12345678912')

		cy.dollyGet(CypressSelector.BUTTON_FLYTT_PERSONER_AVBRYT).click()
	})
})

import { CypressSelector } from '../mocks/Selectors'

describe('Minside mal testing', () => {
	it('passes', () => {
		cy.intercept('DELETE', '*', cy.spy().as('slett_mal'))
		cy.intercept('POST', '*', cy.spy().as('send_oenske'))

		cy.visit('http://localhost:5678')

		cy.dollyGet(CypressSelector.BUTTON_PROFIL).click()
		cy.dollyGet(CypressSelector.BUTTON_PROFIL_MINSIDE).click()

		cy.dollyGet(CypressSelector.BUTTON_OPEN_FORBEDRING_MODAL).click()

		cy.dollyGet(CypressSelector.BUTTON_SEND_FORBEDRINGSOENSKE).should('be.disabled')

		cy.dollyGet(CypressSelector.INPUT_FORBEDRING_MODAL).type('When you wish upon a star')

		cy.dollyGet(CypressSelector.CHECKBOX_FORBEDRING_ANONYM).click()
		cy.dollyGet(CypressSelector.CHECKBOX_FORBEDRING_ANONYM).should('be.enabled')
		cy.dollyGet(CypressSelector.CHECKBOX_FORBEDRING_ANONYM).click()

		cy.dollyGet(CypressSelector.BUTTON_SEND_FORBEDRINGSOENSKE).click()

		cy.dollyGet(CypressSelector.INPUT_MINSIDE_MAL).type('mal')
		cy.dollyGet(CypressSelector.INPUT_MINSIDE_MAL).clear()
		cy.dollyGet(CypressSelector.BUTTON_MALER_SLETT).click()

		cy.get('@send_oenske').should('have.been.calledOnce')
		cy.get('@slett_mal').should('have.been.calledOnce')
	})
})

import { CypressSelector } from '../mocks/Selectors'

describe('Minside mal testing', () => {
	it('passes', () => {
		cy.intercept('DELETE', '*', cy.spy().as('slett_mal'))

		cy.visit('http://localhost:5678/minside')

		cy.dollyGet(CypressSelector.INPUT_MINSIDE_MAL).type('mal')
		cy.dollyGet(CypressSelector.INPUT_MINSIDE_MAL).clear()
		cy.dollyGet(CypressSelector.BUTTON_MALER_SLETT).click()

		cy.get('@slett_mal').should('have.been.calledOnce')
	})
})

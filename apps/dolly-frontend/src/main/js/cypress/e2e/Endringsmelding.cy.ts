import { CypressSelector } from '../mocks/Selectors'

describe('Navigering til endringsmelding', () => {
	it('passes', () => {
		cy.visit('gruppe')

		cy.dollyGet(CypressSelector.BUTTON_HEADER_ENDRINGSMELDING).click()
		cy.url().should('include', '/endringsmelding')
	})
})

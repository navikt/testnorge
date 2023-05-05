import { CypressSelector } from '../mocks/Selectors'

describe('Navigering til endringsmelding', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/gruppe')

		cy.dollyGet(CypressSelector.BUTTON_HEADER_ENDRINGSMELDING).click()
		cy.url().should('include', '/endringsmelding')
	})
})

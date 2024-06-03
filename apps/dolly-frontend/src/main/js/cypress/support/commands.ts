// type definitions for custom commands like "dollyGet"
// will resolve to "cypress/support/commands.ts"
import { CypressSelector } from '../mocks/Selectors'

declare global {
	namespace Cypress {
		interface Chainable {
			/**
			 * Henter dolly component basert på CypressSelector Enum og returner Cypress Chainable for videre testing
			 * @example
			 * cy.dollyGet(CypressSelector.BUTTON_OPPRETT)
			 */
			dollyGet(selector: CypressSelector): Chainable

			/**
			 * Skriver tekst til en dolly komponent og returner Cypress Chainable for videre testing
			 * @example
			 * cy.dollyType(CypressSelector.BUTTON_OPPRETT, "text to input")
			 */
			dollyType(selector: CypressSelector, text: string): Chainable
		}
	}
}

Cypress.Commands.add('dollyGet', (selector: CypressSelector) => {
	return cy.get(`[data-testid="${selector}"]`)
})
Cypress.Commands.add('dollyType', (selector: CypressSelector, textInput: string) => {
	const selected = cy.get(`[data-testid="${selector}"]`).click({ force: true }).focused()
	cy.get('body').type(textInput)
	return selected
})

// type definitions for custom commands like "dollyGet"
// will resolve to "cypress/support/commands.ts"
import { CypressSelector } from '../mocks/Selectors'

declare global {
	namespace Cypress {
		interface Chainable {
			/**
			 * Get dolly component using CypressSelector Enum
			 * @example
			 * cy.dollyGet(CypressSelector.SOME_ENUM)
			 */
			dollyGet(selector: CypressSelector): Chainable
		}
	}
}

Cypress.Commands.add('dollyGet', (selector: CypressSelector) => {
	return cy.get(`[data-cy="${selector}"]`)
})

// ***********************************************************
// This example support/component.ts is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

import './commands'

import { mount } from 'cypress/react18'

declare global {
	namespace Cypress {
		interface Chainable {
			mount: typeof mount
		}
	}
}

Cypress.Commands.add('mount', mount)

// Example use:
// cy.mount(<MyComponent />)

import { defineConfig } from 'cypress'

export default defineConfig({
	defaultCommandTimeout: 25000,
	env: {
		'cypress-react-selector': {
			root: '#root',
		},
	},
	e2e: {
		setupNodeEvents() {},
		specPattern: 'cypress/e2e/**/*.cy.{js,ts,jsx,tsx}',
		excludeSpecPattern: ['**/__snapshots__/*', '**/__image_snapshots__/*'],
	},
})

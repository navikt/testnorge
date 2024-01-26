import { defineConfig } from 'cypress'

export default defineConfig({
	pageLoadTimeout: 120000,
	defaultCommandTimeout: 25000,
	numTestsKeptInMemory: 3,

	env: {
		'cypress-react-selector': {
			root: '#root',
		},
	},

	e2e: {
		setupNodeEvents() {},
		experimentalRunAllSpecs: true,
		specPattern: 'cypress/e2e/**/*.cy.{js,ts,jsx,tsx}',
		excludeSpecPattern: ['**/__snapshots__/*', '**/__image_snapshots__/*'],
	},

	component: {
		devServer: {
			framework: 'react',
			bundler: 'vite',
		},
	},
})

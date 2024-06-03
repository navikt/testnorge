import { defineConfig } from 'cypress'
import pkg from '@neuralegion/cypress-har-generator'

const { install } = pkg

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
		baseUrl: 'http://localhost:5678',
		setupNodeEvents(on) {
			install(on)
		},
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

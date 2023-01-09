/// <reference types="cypress" />

import { varslingerVelkommenResponseMock } from '../mocks/BasicMocks'

describe('Login side og aksepter varsling', () => {
	it('passes', () => {
		cy.intercept(
			{ method: 'GET', url: new RegExp(/testnav-varslinger-service\/api\/v1\/varslinger/) },
			varslingerVelkommenResponseMock
		)
		cy.visit('http://localhost:5678/login')

		cy.get('button').contains('NAV').click()
		cy.get('button').contains('Lukk').click()
	})
})

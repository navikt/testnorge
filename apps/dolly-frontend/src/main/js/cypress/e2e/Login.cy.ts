/// <reference types="cypress" />

import { varslingerVelkommenResponseMock } from '../mocks/BasicMocks'

describe('Login side og aksepter varsling', () => {
	it('passes', () => {
		cy.intercept(
			{ method: 'GET', url: new RegExp(/testnav-varslinger-service\/api\/v1\/varslinger/) },
			varslingerVelkommenResponseMock
		)
		cy.visit('http://localhost:5678/login')

		cy.get('[data-cy="button_login_nav"]').click()
		cy.get('[data-cy="button_varsling_lukk"]').click()
	})
})

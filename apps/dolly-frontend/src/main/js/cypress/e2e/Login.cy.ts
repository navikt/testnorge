import { varslingerVelkommenResponseMock } from '../mocks/BasicMocks'
import { CypressSelector } from '../mocks/Selectors'

describe('Login side og aksepter varsling', () => {
	it('passes', () => {
		cy.intercept(
			{ method: 'GET', url: new RegExp(/testnav-varslinger-service\/api\/v1\/varslinger/) },
			varslingerVelkommenResponseMock
		)
		cy.visit('http://localhost:5678/login')

		cy.dollyGet(CypressSelector.BUTTON_LOGIN_NAV).click()
		cy.dollyGet(CypressSelector.BUTTON_VARSLING_LUKK).click()
	})
})

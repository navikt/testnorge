import { CypressSelector } from '../mocks/Selectors'

describe('Tester at bruker blir sendt til login side dersom man ikke er autorisert', () => {
	it('passes', () => {
		const current = new RegExp(/current/)
		cy.intercept({ method: 'GET', url: current }, { statusCode: 401 })
		cy.visit('gruppe', { failOnStatusCode: false })

		cy.url().should('include', 'login')
		cy.dollyGet(CypressSelector.BUTTON_LOGIN_NAV).should('exist')
	})
})

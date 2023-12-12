import { CypressSelector } from '../mocks/Selectors'

describe('Dolly-søk testing', () => {
	const dollySoekIdenter = new RegExp(/dolly-backend\/api\/v1\/elastic\/identer/)
	it('passes', () => {
		cy.visit('http://localhost:5678')

		cy.dollyGet(CypressSelector.BUTTON_HEADER_FINNPERSON).click()
		cy.dollyGet(CypressSelector.BUTTON_HEADER_DOLLYSOEK).click()

		cy.dollyGet(CypressSelector.EXPANDABLE_PERSONINFORMASJON).click()
		cy.dollyGet(CypressSelector.TOGGLE_HAR_VERGE).click()
		cy.wait(200)

		cy.get('div').contains('Testytest').invoke('show').click()
		cy.wait(1000)

		cy.intercept({ method: 'POST', url: dollySoekIdenter }, [])
		cy.dollyGet(CypressSelector.BUTTON_NULLSTILL_SOEK).click()
		cy.get('div').contains('Ingen søk er gjort')
		cy.wait(1000)

		cy.intercept({ method: 'POST', url: dollySoekIdenter }, ['12345678912'])

		cy.get('.select-kjoenn__control').click().get('.select-kjoenn__menu').click()
		cy.wait(200)

		cy.dollyGet(CypressSelector.BUTTON_VIS_I_GRUPPE).click()
		cy.wait(500)
		cy.get('h1').contains('Testytest')
	})
})

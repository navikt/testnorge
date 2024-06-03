import { CypressSelector } from '../mocks/Selectors'
import { testnorgeMalBestillinger } from '../mocks/BasicMocks'

describe('Dolly Bestillingsstatus testing', () => {
	it('passes', () => {
		const hentGruppeBestilling = new RegExp(/dolly-backend\/api\/v1\/bestilling\/gruppe\/1/)
		cy.intercept({ method: 'GET', url: hentGruppeBestilling }, testnorgeMalBestillinger)
		cy.visit('gruppe')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.TOGGLE_VISNING_BESTILLINGER).click()

		cy.get(':nth-child(2) > .dot-body-row > .dot-body-row-columns').click()
	})
})

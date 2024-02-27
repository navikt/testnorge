import { CypressSelector } from '../mocks/Selectors'

describe('Dolly Bestillingsdetaljer testing', () => {
	it('passes', () => {
		cy.visit('gruppe')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.TOGGLE_VISNING_BESTILLINGER).click()

		cy.get(':nth-child(2) > .dot-body-row > .dot-body-row-columns').click()
		cy.dollyGet(CypressSelector.BUTTON_BESTILLINGDETALJER_GJENOPPRETT).should('be.disabled')
		cy.get(':nth-child(2) > .dot-body-row > .dot-body-row-columns').click()

		cy.get(':nth-child(3) > .dot-body-row > .dot-body-row-columns').click()
		cy.dollyGet(CypressSelector.BUTTON_BESTILLINGDETALJER_GJENOPPRETT).click()
		cy.dollyGet(CypressSelector.BUTTON_BESTILLINGDETALJER_GJENOPPRETT_UTFOER).click()

		cy.dollyGet(CypressSelector.BUTTON_BESTILLINGDETALJER_OPPRETT_MAL).click()
		cy.get('#malnavn').type('Testmal')

		cy.dollyGet(CypressSelector.BUTTON_MALMODAL_LAGRE).click()
	})
})

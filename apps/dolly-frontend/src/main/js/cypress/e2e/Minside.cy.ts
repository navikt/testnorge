import { CypressSelector } from '../mocks/Selectors'
import { brukerMalerEndretMock } from '../mocks/BasicMocks'

const brukerMaler = new RegExp(/dolly-backend\/api\/v1\/malbestilling\?brukerId/)

describe('Minside mal testing', () => {
	it('passes', () => {
		cy.intercept('DELETE', '*', cy.spy().as('slett_mal'))
		cy.intercept('PUT', '*', cy.spy().as('endre_malnavn'))
		cy.intercept('POST', '*', cy.spy().as('send_oenske'))

		cy.visit('')

		cy.dollyGet(CypressSelector.BUTTON_PROFIL).click()
		cy.dollyGet(CypressSelector.BUTTON_PROFIL_MINSIDE).click()

		cy.dollyGet(CypressSelector.BUTTON_OPEN_FORBEDRING_MODAL).click()

		cy.dollyGet(CypressSelector.BUTTON_SEND_FORBEDRINGSOENSKE).should('be.disabled')

		cy.dollyType(CypressSelector.INPUT_FORBEDRING_MODAL, 'When you wish upon a star')

		cy.dollyGet(CypressSelector.CHECKBOX_FORBEDRING_ANONYM).click()
		cy.dollyGet(CypressSelector.CHECKBOX_FORBEDRING_ANONYM).should('be.enabled')
		cy.dollyGet(CypressSelector.CHECKBOX_FORBEDRING_ANONYM).click()

		cy.dollyGet(CypressSelector.BUTTON_SEND_FORBEDRINGSOENSKE).click()

		cy.dollyGet(CypressSelector.INPUT_MINSIDE_SOEK_MAL).type('mal')
		cy.dollyGet(CypressSelector.INPUT_MINSIDE_SOEK_MAL).clear()

		cy.dollyGet(CypressSelector.TOGGLE_MIN_SIDE_ORGANISASJON_MALER).click()
		cy.dollyGet(CypressSelector.TOGGLE_MIN_SIDE_PERSONER_MALER).click()
		cy.dollyGet(CypressSelector.BUTTON_MALER_SLETT).click()
		cy.dollyGet(CypressSelector.BUTTON_MALER_SLETT_BEKREFT).click()

		cy.dollyGet(CypressSelector.BUTTON_MINSIDE_ENDRE_MALNAVN).click()
		cy.dollyGet(CypressSelector.INPUT_MINSIDE_ENDRE_MALNAVN).clear()
		cy.dollyType(CypressSelector.INPUT_MINSIDE_ENDRE_MALNAVN, 'Nytt navn på mal')

		cy.intercept({ method: 'GET', url: brukerMaler }, brukerMalerEndretMock)

		cy.dollyGet(CypressSelector.BUTTON_MINSIDE_LAGRE_MALNAVN).click()

		cy.get('@send_oenske').should('have.been.calledOnce')
		cy.get('@slett_mal').should('have.been.calledOnce')
		cy.get('@endre_malnavn').should('have.been.calledOnce')
	})
})

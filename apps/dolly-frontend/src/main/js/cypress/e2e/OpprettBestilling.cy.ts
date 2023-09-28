import { CypressSelector } from '../mocks/Selectors'

describe('Oppretter bestilling med alle artifakter som er avhengige av Q1 eller Q2 og sjekker at disse blir huket av', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/gruppe/1')

		cy.dollyGet(CypressSelector.BUTTON_OPPRETT_PERSONER).click()
		cy.dollyGet(CypressSelector.BUTTON_START_BESTILLING).click()
		cy.dollyGet(CypressSelector.BUTTON_FJERN_MILJOE_AVHENGIG).as('fjernMiljoeAvhengige')
		cy.dollyGet(CypressSelector.BUTTON_VELG_MILJOE_AVHENGIG)
			.as('velgMiljoeAvhengige')
			.each((element, index) => {
				cy.get('@velgMiljoeAvhengige').eq(index).click()
				cy.dollyGet(CypressSelector.BUTTON_VIDERE).click()
				cy.dollyGet(CypressSelector.BUTTON_VIDERE).click()
				cy.get('#q1').should('be.checked')
				cy.get('#q2').should('be.checked')
				cy.get('#q4').should('not.be.checked')
				cy.dollyGet(CypressSelector.BUTTON_TILBAKE).click()
				cy.dollyGet(CypressSelector.BUTTON_TILBAKE).click()
				cy.get('@fjernMiljoeAvhengige').eq(index).click()
			})
	})
})

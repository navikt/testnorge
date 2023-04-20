import { CypressSelector } from '../mocks/Selectors'

describe('Åpne bestilt ident med knytning mot alle fagsystem', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/gruppe')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.BUTTON_FLYTT_PERSONER).click()

		cy.dollyGet(CypressSelector.TOGGLE_NY_GRUPPE).click()
		cy.dollyGet(CypressSelector.TOGGLE_EKSISTERENDE_GRUPPE).click()

		cy.dollyGet(CypressSelector.BUTTON_FLYTT_PERSONER_AVBRYT).click()

		cy.dollyGet(CypressSelector.TOGGLE_VISNING_BESTILLINGER).click()
		cy.wait(200)

		cy.dollyGet(CypressSelector.BUTTON_OPEN_BESTILLING).each((element) => {
			cy.wrap(element).click()
		})

		cy.dollyGet(CypressSelector.TOGGLE_VISNING_PERSONER).click()

		cy.dollyGet(CypressSelector.BUTTON_OPEN_IDENT).click()

		cy.dollyGet(CypressSelector.BUTTON_OPEN_BESTILLINGSDETALJER)
		cy.wait(200)
		cy.dollyGet(CypressSelector.TITLE_VISNING).invoke('show').click()
		cy.wait(200)

		cy.dollyGet(CypressSelector.MILJOE_HOVER).each((element) => {
			cy.wrap(element).invoke('show').click()
			cy.wait(400)
			cy.dollyGet(CypressSelector.TITLE_VISNING).invoke('show').click()
		})
	})
})

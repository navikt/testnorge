import { CypressSelector } from '../mocks/Selectors'

describe('Åpne bestilt ident med knytning mot alle fagsystem', () => {
	it('passes', () => {
		cy.intercept('PUT', '*', cy.spy().as('toggle_brukt'))

		cy.visit('http://localhost:5678/gruppe')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.TOGGLE_VISNING_BESTILLINGER).click()
		cy.wait(200)

		cy.dollyGet(CypressSelector.BUTTON_OPEN_BESTILLING).each((element) => {
			cy.wrap(element).click()
		})

		cy.dollyGet(CypressSelector.TOGGLE_VISNING_PERSONER).click()

		cy.dollyGet(CypressSelector.TOGGLE_PERSON_IBRUK).click()
		cy.dollyGet(CypressSelector.TOGGLE_PERSON_IBRUK).should('be.enabled')
		cy.dollyGet(CypressSelector.TOGGLE_PERSON_IBRUK).click()

		cy.dollyGet(CypressSelector.BUTTON_OPEN_IDENT).click()

		cy.dollyGet(CypressSelector.BUTTON_OPEN_BESTILLINGSDETALJER)
		cy.wait(200)
		cy.dollyGet(CypressSelector.TITLE_VISNING).invoke('show').click()
		cy.wait(1000)

		cy.wait(1000)
		cy.dollyGet(CypressSelector.BUTTON_OPEN_EXPANDABLE).each((element) => {
			cy.wrap(element).invoke('show').click()
			cy.wait(300)
		})
		cy.wait(1000)

		cy.wait(1000)
		cy.dollyGet(CypressSelector.HOVER_MILJOE).each((element) => {
			cy.wrap(element).invoke('show').click()
			cy.wait(400)
			cy.dollyGet(CypressSelector.TITLE_VISNING).invoke('show').click()
		})

		cy.get('@toggle_brukt').should('be.calledTwice')
	})
})

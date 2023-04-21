import { CypressSelector } from '../mocks/Selectors'

describe('Test-Norge søk testing', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678')

		cy.dollyGet(CypressSelector.BUTTON_HEADER_TESTNORGE).click()

		cy.dollyType(CypressSelector.INPUT_TESTNORGE_FNR, '123456')
		cy.dollyGet(CypressSelector.TITLE_TESTNORGE).invoke('show').click()

		cy.get('.skjemaelement__feilmelding').should('exist')

		cy.dollyGet(CypressSelector.INPUT_TESTNORGE_FNR).clear()

		cy.wait(200)

		cy.get('.skjemaelement__feilmelding').should('not.exist')

		cy.dollyGet(CypressSelector.BUTTON_HEADER_PERSONER).click()
		cy.get('div').contains('Testytest').click()
		cy.dollyGet(CypressSelector.BUTTON_IMPORTER_PERSONER).click()

		cy.dollyGet(CypressSelector.TITLE_TESTNORGE).should('exist')
	})
})

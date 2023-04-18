import { CypressSelector } from '../mocks/Selectors'

describe('Test-Norge søk testing', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/testnorge')

		cy.dollyGet(CypressSelector.INPUT_TESTNORGE_FNR).type('123456')
		cy.dollyGet(CypressSelector.TITLE_TESTNORGE).invoke('show').click()

		cy.get('.skjemaelement__feilmelding').should('exist')

		cy.dollyGet(CypressSelector.INPUT_TESTNORGE_FNR).clear()

		cy.wait(200)

		cy.get('.skjemaelement__feilmelding').should('not.exist')
	})
})

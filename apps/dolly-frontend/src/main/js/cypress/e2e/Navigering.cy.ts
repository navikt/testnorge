import { CypressSelector } from '../mocks/Selectors'
import { personFragmentNavigerMock } from '../mocks/BasicMocks'
import { ERROR_NAVIGATE_IDENT } from '../../src/ducks/errors/ErrorMessages'

const personFragmentNaviger = new RegExp(/dolly-backend\/api\/v1\/ident\/naviger\/12345678912/)

describe('Navigering til ident som finnes i bestilling og tilbake igjen til bestillingen', () => {
	it('passes', () => {
		cy.visit('gruppe')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.TOGGLE_VISNING_BESTILLINGER).click()

		cy.dollyGet(CypressSelector.BUTTON_OPEN_BESTILLING).each((element) => {
			cy.wrap(element).click()
		})

		cy.get('Button').contains('12345678912').click()

		cy.dollyGet(CypressSelector.TOGGLE_VISNING_PERSONER).should('have.attr', 'aria-checked', 'true')
		cy.dollyGet(CypressSelector.BUTTON_TIDLIGEREBESTILLINGER_NAVIGER).click()

		cy.dollyGet(CypressSelector.TOGGLE_VISNING_BESTILLINGER).should(
			'have.attr',
			'aria-checked',
			'true',
		)
	})
})

describe('Navigering til ident som finnes i gruppe 1', () => {
	it('passes', () => {
		cy.visit('gruppe')

		//Midlertidig not found på navigering til ident etter søk
		cy.intercept({ method: 'GET', url: personFragmentNaviger }, { statusCode: 404 })

		cy.dollyType(CypressSelector.INPUT_DOLLY_SOEK, '12345')
		cy.dollyGet(CypressSelector.BUTTON_NAVIGER_DOLLY).click()
		cy.wait(400)

		cy.dollyGet(CypressSelector.ERROR_MESSAGE_NAVIGERING).should(
			'contains.text',
			ERROR_NAVIGATE_IDENT,
		)

		//Korrekt navigering igjen
		cy.intercept({ method: 'GET', url: personFragmentNaviger }, personFragmentNavigerMock)

		cy.dollyGet(CypressSelector.TOGGLE_SEARCH_BESTILLING).click()
		cy.dollyType(CypressSelector.INPUT_DOLLY_SOEK, '1')
		cy.dollyGet(CypressSelector.BUTTON_NAVIGER_DOLLY).click()
		cy.dollyGet(CypressSelector.TOGGLE_SEARCH_PERSON).click()

		cy.dollyType(CypressSelector.INPUT_DOLLY_SOEK, '12345')
		cy.dollyGet(CypressSelector.BUTTON_NAVIGER_DOLLY).click()
		cy.wait(400)

		cy.url().should('include', '/gruppe/1')
	})
})

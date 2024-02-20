import { CypressSelector } from '../mocks/Selectors'

describe('Testing av forskjellige actions på gruppeheaderen', () => {
	it('Legg til på alle i gruppe', () => {
		cy.visit('gruppe')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.BUTTON_LEGGTILPAAALLE).click()

		cy.dollyGet(CypressSelector.BUTTON_VIDERE).click()

		cy.dollyGet(CypressSelector.BUTTON_VIDERE).click()

		cy.dollyGet(CypressSelector.BUTTON_FULLFOER_BESTILLING).click()
	})

	const tagsPost = new RegExp(/dolly-backend\/api\/v1\/tags/)
	it('Posting av tags', () => {
		cy.visit('gruppe')
		cy.intercept({ method: 'POST', url: tagsPost }, { statusCode: 201 }).as('postTags')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.BUTTON_TILKNYTT_TAGS).click()
		cy.get('.select__input-container').type('DUMMY{enter}')

		cy.dollyGet(CypressSelector.BUTTON_POST_TAGS).click()

		cy.wait('@postTags').its('response.statusCode').should('eq', 201)

		cy.get('h1').contains('Testytest')
	})

	it('Flyttpersoner funksjonalitet', () => {
		cy.visit('gruppe')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.BUTTON_FLYTT_PERSONER).click()

		cy.dollyGet(CypressSelector.TOGGLE_ALLE_GRUPPER).click()
		cy.dollyGet(CypressSelector.TOGGLE_EKSISTERENDE_GRUPPE).click()
		cy.dollyGet(CypressSelector.TOGGLE_NY_GRUPPE).click()

		cy.dollyGet(CypressSelector.INPUT_NY_GRUPPE_NAVN).type('TestNavn')
		cy.dollyGet(CypressSelector.INPUT_NY_GRUPPE_HENSIKT).type('TestHensikt')
		cy.dollyGet(CypressSelector.BUTTON_NY_GRUPPE_OPPRETT).click()

		cy.get('.navds-checkbox__label').contains('12345678912').click()

		cy.dollyGet(CypressSelector.CONTAINER_VALGTE_PERSONER).should('contain', '12345678912')
		cy.dollyGet(CypressSelector.BUTTON_FLYTT_PERSONER_NULLSTILL).click()
		cy.dollyGet(CypressSelector.CONTAINER_VALGTE_PERSONER).should('not.contain', '12345678912')

		cy.dollyGet(CypressSelector.BUTTON_FLYTT_PERSONER_AVBRYT).click()
	})

	it('Gjenopprett gruppe funksjonalitet', () => {
		cy.visit('gruppe')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.BUTTON_GJENOPPRETT_GRUPPE).click()

		cy.get('#q2').click()

		cy.dollyGet(CypressSelector.BUTTON_BESTILLINGDETALJER_GJENOPPRETT_UTFOER).click()
	})

	it('Rediger gruppe funksjonalitet', () => {
		cy.visit('gruppe')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.BUTTON_REDIGER_GRUPPE).click()

		cy.dollyGet(CypressSelector.INPUT_NAVN).clear().type('Redigert navn')
		cy.dollyGet(CypressSelector.INPUT_HENSIKT).clear().type('Redigert hensikt')

		cy.dollyGet(CypressSelector.BUTTON_OPPRETT).click()
	})
})

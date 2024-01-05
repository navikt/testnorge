import { CypressSelector } from '../mocks/Selectors'

describe('Dolly tags testing', () => {
	const tagsPost = new RegExp(/dolly-backend\/api\/v1\/tags/)
	it('passes', () => {
		cy.visit('gruppe')
		cy.intercept({ method: 'POST', url: tagsPost }, { statusCode: 201 }).as('postTags')

		cy.get('div').contains('Testytest').click()

		cy.dollyGet(CypressSelector.BUTTON_TILKNYTT_TAGS).click()
		cy.get('.select__input-container').type('DUMMY{enter}')

		cy.dollyGet(CypressSelector.BUTTON_POST_TAGS).click()

		cy.wait('@postTags').its('response.statusCode').should('eq', 201)

		cy.get('h1').contains('Testytest')
	})
})

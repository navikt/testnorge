import { CypressSelector } from '../mocks/Selectors'
import {
	avbruttBestillingMock,
	uferdigBestillingMock,
	uferdigeBestillingerMock,
} from '../mocks/BasicMocks'

const uferdigBestilling = new RegExp(/dolly-backend\/api\/v1\/bestilling\/2$/)
const uferdigeBestillinger = new RegExp(/dolly-backend\/api\/v1\/bestilling\/gruppe\/2\/ikkeferdig/)
const personFragmentNaviger = new RegExp(/dolly-backend\/api\/v1\/ident\/naviger\/12345678912/)

describe('Opprett gruppe og start bestilling med alle mulige tilvalg', () => {
	it('passes', () => {
		cy.visit('http://localhost:5678/gruppe')

		// Naviger mellom tabs
		cy.dollyGet(CypressSelector.TOGGLE_FAVORITTER).click()
		cy.dollyGet(CypressSelector.TOGGLE_ALLE).click()
		cy.dollyGet(CypressSelector.TOGGLE_MINE).click()

		// Opprett ny gruppe
		cy.dollyGet(CypressSelector.BUTTON_NY_GRUPPE).click()
		cy.dollyGet(CypressSelector.INPUT_NAVN).type('Testing med Cypress')
		cy.dollyGet(CypressSelector.INPUT_HENSIKT).type('Masse testing med Cypress')
		cy.dollyGet(CypressSelector.BUTTON_OPPRETT).click()
		cy.dollyGet(CypressSelector.BUTTON_OPPRETT_PERSONER).click()
		cy.dollyGet(CypressSelector.TOGGLE_EKSISTERENDE_PERSON).click()
		cy.dollyGet(CypressSelector.TOGGLE_NY_PERSON).click()
		cy.dollyGet(CypressSelector.TOGGLE_MAL).click()

		cy.url().should('include', '/gruppe/2')

		cy.dollyGet(CypressSelector.BUTTON_START_BESTILLING).click()
		cy.dollyGet(CypressSelector.BUTTON_VELG_ALLE).each((btn) => cy.wrap(btn).click())
		cy.dollyGet(CypressSelector.BUTTON_VELG_MILJOE_AVHENGIG).each((btn) => cy.wrap(btn).click())

		cy.dollyGet(CypressSelector.BUTTON_VIDERE).click()
		cy.wait(500)

		cy.dollyGet(CypressSelector.BUTTON_TILBAKE).click()
		cy.dollyGet(CypressSelector.BUTTON_FJERN_ALLE).each((btn) => cy.wrap(btn).click())

		cy.dollyGet(CypressSelector.BUTTON_VIDERE).click()
		cy.dollyGet(CypressSelector.BUTTON_VIDERE).click()

		cy.dollyGet(CypressSelector.TOGGLE_BESTILLING_MAL).click()
		cy.dollyGet(CypressSelector.TOGGLE_BESTILLING_MAL).should('be.enabled')

		cy.dollyType(CypressSelector.INPUT_BESTILLING_MALNAVN, 'Fornuftig navn på mal')

		//Midlertidig aktiv bestilling intercept
		cy.intercept({ method: 'GET', url: uferdigBestilling }, uferdigBestillingMock)
		cy.intercept({ method: 'GET', url: uferdigeBestillinger }, uferdigeBestillingerMock)

		cy.dollyGet(CypressSelector.TITLE_SEND_KOMMENTAR).click()
		cy.dollyGet(CypressSelector.BUTTON_FULLFOER_BESTILLING).click()

		cy.wait(1000)

		//Avbrutt bestilling intercept
		cy.intercept({ method: 'GET', url: uferdigBestilling }, avbruttBestillingMock)

		cy.dollyGet(CypressSelector.BUTTON_AVBRYT_BESTILLING).click()
		cy.wait(500)
		cy.dollyGet(CypressSelector.BUTTON_LUKK_BESTILLING_RESULTAT).click()
	})
})

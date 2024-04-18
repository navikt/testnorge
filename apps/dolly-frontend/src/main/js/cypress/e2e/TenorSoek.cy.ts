import { CypressSelector } from '../mocks/Selectors'
import {
	responseFalse,
	responseTrue,
	tenorSoekOversiktMock,
	tenorSoekTestdataMock,
} from '../mocks/BasicMocks'

describe('Tenor-søk testing', () => {
	const tenorSoekOversikt = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\/oversikt\?antall=10&side=0/,
	)
	const tenorSoekTestdata = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\?kilde=FREG&type=AlleFelter/,
	)
	const dollyBackendFinnesTrue = new RegExp(/dolly-backend\/api\/v1\/ident\/finnes\/12345678912/)
	const dollyBackendFinnesFalse = new RegExp(/dolly-backend\/api\/v1\/ident\/finnes\/98765432198/)

	it('passes', () => {
		cy.visit('')

		cy.intercept({ method: 'POST', url: tenorSoekOversikt }, tenorSoekOversiktMock)
		cy.intercept({ method: 'POST', url: tenorSoekTestdata }, tenorSoekTestdataMock)
		cy.intercept({ method: 'GET', url: dollyBackendFinnesTrue }, responseTrue)
		cy.intercept({ method: 'GET', url: dollyBackendFinnesFalse }, responseFalse)

		// Naviger til Tenor-soek og gjoer et soek
		cy.dollyGet(CypressSelector.BUTTON_HEADER_FINNPERSON).click()
		cy.dollyGet(CypressSelector.BUTTON_HEADER_TENOR).click()
		cy.get('h1').contains('Søk etter personer i Tenor').should('exist')
		cy.dollyGet(CypressSelector.CHECKBOX_TENORSOEK).click()
		cy.wait(1000)

		// Velg person som ikke ligger i Dolly og start import av personen
		cy.get('div').contains('TIGER ULV').click()
		cy.get('h2').contains('TIGER ULV').should('exist')
		cy.dollyGet(CypressSelector.BUTTON_IMPORTER_PERSONER).click()
		cy.wait(500)
		cy.get('h1').contains('Importer person').should('exist')
		cy.dollyGet(CypressSelector.BUTTON_IMPORTER).click()
		cy.wait(500)
		cy.get('.bestillingsveileder').should('exist')
		cy.dollyGet(CypressSelector.BUTTON_AVBRYT).click()
		cy.wait(500)
		cy.dollyGet(CypressSelector.BUTTON_BEKREFT).click()
		cy.wait(1000)
		cy.get('h1').contains('Søk etter personer i Tenor').should('exist')

		// Naviger til foerste person som ligger i Dolly
		cy.dollyGet(CypressSelector.BUTTON_VIS_I_GRUPPE).first().click()
		cy.wait(500)
		cy.get('h1').contains('Testytest').should('exist')

		// Gaa til soek fra gruppe
		cy.dollyGet(CypressSelector.BUTTON_IMPORTER_PERSONER).click()
		cy.get('h1').contains('Søk etter personer i Tenor').should('exist')
	})
})

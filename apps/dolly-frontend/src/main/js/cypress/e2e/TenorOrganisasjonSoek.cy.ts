import { CypressSelector } from '../mocks/Selectors'
import {
	tenorSoekOrganisasjonOversiktMock,
	tenorSoekOrganisasjonTestdataMock,
} from '../mocks/BasicMocks'

describe('Tenor-søk testing', () => {
	const tenorSoekOrganisasjonOversikt = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\/organisasjoner\/oversikt\?antall=10&side=0/,
	)
	const tenorSoekOrganisasjonTestdata = new RegExp(
		/testnav-tenor-search-service\/api\/v1\/tenor\/testdata\/organisasjoner\?type=Organisasjon/,
	)

	it('passes', () => {
		cy.visit('')

		cy.intercept(
			{ method: 'POST', url: tenorSoekOrganisasjonOversikt },
			tenorSoekOrganisasjonOversiktMock,
		)
		cy.intercept(
			{ method: 'POST', url: tenorSoekOrganisasjonTestdata },
			tenorSoekOrganisasjonTestdataMock,
		)

		// Naviger til Tenor-organisasjon-soek og post et soek
		cy.dollyGet(CypressSelector.BUTTON_HEADER_ORGANISASJONER).click()
		cy.dollyGet(CypressSelector.BUTTON_HEADER_TENOR_ORGANISASJONER).click()
		cy.get('h1').contains('Søk etter organisasjoner i Tenor').should('exist')
		cy.dollyGet(CypressSelector.CHECKBOX_ORGANISASJONER_TENORSOEK).click()

		cy.get('div').contains('TIGER').click()
		cy.get('h2').contains('TIGER').should('exist')

		// Sjekk at antall valgt er 1, deretter clear soeket og sjekk at antall valgt er 0
		cy.dollyGet(CypressSelector.TITLE_TENOR_ORGANISASJONER_FORETAKSREGISTERET)
			.should('contain.text', 'Enhetsregisteret og Foretaksregisteret')
			.should('contain.text', '1')

		cy.dollyGet(CypressSelector.BUTTON_TENOR_CLEAR_HEADER).click({ multiple: true })

		cy.dollyGet(CypressSelector.TITLE_TENOR_ORGANISASJONER_FORETAKSREGISTERET).should(
			'not.contain.text',
			'1',
		)
	})
})

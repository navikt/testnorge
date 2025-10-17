import { test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { gjeldendeBankidBrukerMock, personOrgTilgangMock } from '#/mocks/BasicMocks'

test('Bankid testing', async ({ page }) => {
	await page.route(new RegExp(/current/), async (route) => {
		await route.fulfill({
			status: 200,
			body: JSON.stringify(gjeldendeBankidBrukerMock),
			headers: { 'content-type': 'application/json' },
		})
	})
	await page.route(new RegExp(/altinn\/organisasjoner/), async (route) => {
		await route.fulfill({
			status: 200,
			body: JSON.stringify(personOrgTilgangMock),
			headers: { 'content-type': 'application/json' },
		})
	})
	await page.goto('login')
	await page.getByTestId(TestComponentSelectors.BUTTON_LOGIN_BANKID).click()
	await page.goto('bruker')
	await page.getByTestId(TestComponentSelectors.BUTTON_VELG_ORG_BRUKER).click()
})

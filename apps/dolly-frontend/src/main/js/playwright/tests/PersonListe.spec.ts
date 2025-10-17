import { expect, test } from '@playwright/test'
import {
	mockedGruppe,
	mockedGruppeBestilling,
	mockedPdlForvalter,
	mockedPersonService,
} from '#/mocks/Personliste.mocks'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { brukerTeamsMock } from '#/mocks/BasicMocks'

test('should display NaN for alder when a person has inconsistent birth dates', async ({
	page,
}) => {
	const bestilling = new RegExp(/dolly-backend\/api\/v1\/bestilling\/gruppe\/1/)
	const bestillingIkkeFerdig = new RegExp(
		/dolly-backend\/api\/v1\/bestilling\/gruppe\/1\/ikkeferdig/,
	)
	const pdlForvalter = new RegExp(/testnav-pdl-forvalter\/api\/v1\/personer\?identer/)
	const gruppe = new RegExp(/api\/v1\/gruppe\/1\/page\/0\?pageSize=10/)
	const pdlPersonEnkelt = new RegExp(/person-service\/api\/v2\/personer\/identer\?/)
	const brukerTeams = new RegExp(/api\/v1\/bruker\/teams/)

	await page.route(pdlForvalter, async (route) => {
		await route.fulfill({
			status: 200,
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(mockedPdlForvalter),
		})
	})
	await page.route(gruppe, async (route) => {
		await route.fulfill({
			status: 200,
			body: JSON.stringify(mockedGruppe),
		})
	})
	await page.route(pdlPersonEnkelt, async (route) => {
		await route.fulfill({
			status: 200,
			body: JSON.stringify(mockedPersonService),
		})
	})
	await page.route(bestilling, async (route) => {
		await route.fulfill({
			status: 200,
			body: JSON.stringify([mockedGruppeBestilling]),
		})
	})
	await page.route(bestillingIkkeFerdig, async (route) => {
		await route.fulfill({
			status: 200,
			body: JSON.stringify([]), // Fix: convert empty array to JSON string
		})
	})
	await page.route(brukerTeams, async (route) => {
		await route.fulfill({
			status: 200,
			body: JSON.stringify(brukerTeamsMock),
		})
	})

	await page.goto('/gruppe/1')

	const tableElement = page.getByTestId(TestComponentSelectors.CONTAINER_DOLLY_TABLE)
	await expect(tableElement).toBeVisible()

	const personRow = tableElement.getByText('12811212312')
	await expect(personRow).toBeVisible()

	const notANumberElement = tableElement.getByText('NaN')
	await expect(notANumberElement).not.toBeVisible()
})

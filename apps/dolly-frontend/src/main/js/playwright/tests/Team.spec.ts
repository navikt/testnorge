import { TestComponentSelectors } from '#/mocks/Selectors'
import { expect, test } from '#/globalSetup'

test('Naviger til team-oversikt og opprett et team', async ({ page }) => {
	await page.goto('')
	await page.getByTestId(TestComponentSelectors.BUTTON_PROFIL).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_PROFIL_TEAMOVERSIKT).click()
	await page.getByTestId(TestComponentSelectors.EXPANDABLE_TEAM).click()
	await expect(
		page
			.locator('h4')
			.getByText(/Beskrivelse/)
			.first(),
	).toBeVisible()

	await page.getByTestId(TestComponentSelectors.BUTTON_TEAM_OPPRETT).click()
	await page.getByTestId(TestComponentSelectors.INPUT_TEAM_NAVN).fill('Team navn')
	await page.getByTestId(TestComponentSelectors.INPUT_TEAM_BESKRIVELSE).fill('Team beskrivelse')
	await page.getByTestId(TestComponentSelectors.BUTTON_TEAM_SUBMIT).click()
	await expect(page.locator('h1').getByText(/Opprett team/)).not.toBeVisible()
})

import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test('Åpne bestilt ident med knytning mot alle fagsystem', async ({ page }) => {
	await page.goto('gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_BESTILLINGER).click()
	await page.waitForTimeout(2000)
	await page
		.getByTestId(TestComponentSelectors.BUTTON_OPEN_BESTILLING)
		.all()
		.then((elements) => {
			elements.forEach((element) => {
				element.hover()
			})
		})
	await page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_PERSON_IBRUK).click()
	await expect(page.getByTestId(TestComponentSelectors.TOGGLE_PERSON_IBRUK)).toBeChecked()
	await page.getByTestId(TestComponentSelectors.TOGGLE_PERSON_IBRUK).click()
	await expect(page.getByTestId(TestComponentSelectors.TOGGLE_PERSON_IBRUK)).not.toBeChecked()

	await page.getByTestId(TestComponentSelectors.BUTTON_OPEN_IDENT).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_OPEN_BESTILLINGSDETALJER).click()
	await page.waitForTimeout(300)
	await page.getByTestId(TestComponentSelectors.TITLE_VISNING).hover({ force: true })

	await page.getByTestId(TestComponentSelectors.BUTTON_MODAL_CLOSE).click()

	await page.waitForTimeout(1000)

	for (const button_open_expandable of await page
		.getByTestId(TestComponentSelectors.BUTTON_OPEN_EXPANDABLE)
		.all()) {
		await button_open_expandable.click()
		await page.waitForTimeout(200)
	}

	for (const hover_miljoe of await page.getByTestId(TestComponentSelectors.HOVER_MILJOE).all()) {
		await hover_miljoe.click()
		await page.waitForTimeout(200)
		await page.getByTestId(TestComponentSelectors.BUTTON_OPEN_KONTAKTINFO).hover()
		// Hover over kontaktinfo for å lukke gjeldende åpne miljø hover
		await page.waitForTimeout(200)
	}

	await page.getByTestId(TestComponentSelectors.TITLE_VISNING).hover({ force: true })
})

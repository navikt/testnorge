import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { laastGruppeMock } from '#/mocks/BasicMocks'

test('Testing av forskjellige actions på gruppeheaderen', async ({ page }) => {
	await page.goto('gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()
	await page.getByTestId(TestComponentSelectors.BUTTON_LEGGTILPAAALLE).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_FULLFOER_BESTILLING).click()

	// Testing av tags
	await page.goto('gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()

	await page.waitForTimeout(1500)

	await page.route('**/tags', async (route) => {
		await route.fulfill({ status: 201 })
	})

	await page.getByTestId(TestComponentSelectors.BUTTON_TILKNYTT_TAGS).click()
	await page.getByLabel('Velg hvilke tags du ønsker å legge til').click()
	await page.getByRole('option', { name: 'Dummy' }).click()
	await page.getByText('Legg til tags på gruppe').click()
	await page.getByRole('button', { name: 'Tilknytt tags' }).click()
	await expect(
		page
			.locator('h1')
			.getByText(/Testytest/)
			.first(),
	).toBeVisible()

	//Testing av flyttpersoner funksjonalitet
	await page.goto('gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()
	await page.getByTestId(TestComponentSelectors.BUTTON_FLYTT_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_ALLE_GRUPPER).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_EKSISTERENDE_GRUPPE).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_NY_GRUPPE).click()
	await page.getByTestId(TestComponentSelectors.INPUT_NY_GRUPPE_NAVN).fill('TestNavn')
	await page.getByTestId(TestComponentSelectors.INPUT_NY_GRUPPE_HENSIKT).fill('TestHensikt')
	await page.getByTestId(TestComponentSelectors.BUTTON_NY_GRUPPE_OPPRETT).click()
	await page
		.locator('.aksel-checkbox__label')
		.getByText(/12345678912/)
		.first()
		.click()
	await expect(page.getByTestId(TestComponentSelectors.CONTAINER_VALGTE_PERSONER)).toContainText(
		'12345678912',
	)
	await page.getByTestId(TestComponentSelectors.BUTTON_FLYTT_PERSONER_NULLSTILL).click()
	await expect(
		page.getByTestId(TestComponentSelectors.CONTAINER_VALGTE_PERSONER),
	).not.toContainText('12345678912')
	await page.getByTestId(TestComponentSelectors.BUTTON_FLYTT_PERSONER_AVBRYT).click()

	// Testing av gjenopprett gruppe funksjonalitet
	await page.goto('gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()
	await page.getByTestId(TestComponentSelectors.BUTTON_GJENOPPRETT_GRUPPE).click()
	await page.locator('#q2').click()
	await page
		.getByTestId(TestComponentSelectors.BUTTON_BESTILLINGDETALJER_GJENOPPRETT_UTFOER)
		.click()
})

test('Testing av laas gruppe funksjonalitet', async ({ page }) => {
	await page.goto('gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()

	await page.getByRole('button', { name: 'Lås' }).click()

	await expect(page.getByText(/låse denne gruppen/)).toBeVisible()

	// Endrer api kall til å returnere låst gruppe
	await page.route(new RegExp(/\/api\/v1\/gruppe\/1/), async (route) => {
		await route.fulfill({
			status: 200,
			body: JSON.stringify(laastGruppeMock),
		})
	})

	await page.getByRole('button', { name: 'Ja, lås gruppe' }).click()

	await expect(page.getByRole('img', { name: 'locked-group' })).toBeVisible()

	await page.reload()

	await expect(page.getByRole('img', { name: 'locked-group' })).toBeVisible()
})

import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

test('Åpne bestilt ident med knytning mot alle fagsystem', async ({ page }) => {
	test.slow()

	await page.goto('gruppe', { waitUntil: 'networkidle', timeout: 30000 })

	const testytestElement = page
		.locator('div')
		.getByText(/Testytest/)
		.first()
	await expect(testytestElement).toBeVisible()
	await testytestElement.click()

	const toggleVisningBestillinger = page.getByTestId(
		TestComponentSelectors.TOGGLE_VISNING_BESTILLINGER,
	)
	await expect(toggleVisningBestillinger).toBeVisible()
	await expect(toggleVisningBestillinger).toBeEnabled()
	await toggleVisningBestillinger.click()

	await page
		.waitForResponse((response) => response.url().includes('api') && response.status() === 200)
		.catch(() => {
			new Error('Response not received in time')
		})

	await expect(
		page.getByTestId(TestComponentSelectors.BUTTON_OPEN_BESTILLING).first(),
	).toBeVisible()

	const elements = await page.getByTestId(TestComponentSelectors.BUTTON_OPEN_BESTILLING).all()
	for (const element of elements) {
		await expect(element).toBeVisible()
		await element.hover()
	}

	const toggleVisningPersoner = page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_PERSONER)
	await expect(toggleVisningPersoner).toBeVisible()
	await toggleVisningPersoner.click()

	const togglePersonIBruk = page.getByTestId(TestComponentSelectors.TOGGLE_PERSON_IBRUK)
	await expect(togglePersonIBruk).toBeVisible()
	await togglePersonIBruk.click()

	await page.waitForTimeout(100)

	await expect(togglePersonIBruk).toBeEnabled()
	await togglePersonIBruk.click()

	const buttonOpenIdent = page.getByTestId(TestComponentSelectors.BUTTON_OPEN_IDENT)
	await expect(buttonOpenIdent).toBeVisible()
	await buttonOpenIdent.click()

	const buttonOpenBestillingsdetaljer = page.getByTestId(
		TestComponentSelectors.BUTTON_OPEN_BESTILLINGSDETALJER,
	)
	await expect(buttonOpenBestillingsdetaljer).toBeVisible()
	await buttonOpenBestillingsdetaljer.click()

	await page.waitForTimeout(500)

	const titleVisning = page.getByTestId(TestComponentSelectors.TITLE_VISNING)
	await expect(titleVisning).toBeVisible({ timeout: 5000 })

	await page.evaluate((selector) => {
		const element = document.querySelector(`[data-testid="${selector}"]`)
		if (element) {
			const event = new MouseEvent('mouseover', {
				bubbles: true,
				cancelable: true,
			})
			element.dispatchEvent(event)
		}
	}, TestComponentSelectors.TITLE_VISNING)

	await page.waitForTimeout(200)

	const buttonModalClose = page.getByTestId(TestComponentSelectors.BUTTON_MODAL_CLOSE)
	await expect(buttonModalClose).toBeVisible()
	await buttonModalClose.click()

	await page.waitForTimeout(300)

	const expandableButtons = await page
		.getByTestId(TestComponentSelectors.BUTTON_OPEN_EXPANDABLE)
		.all()
	for (const button of expandableButtons) {
		await expect(button).toBeVisible()
		await button.click()

		await page.waitForTimeout(100)
	}

	const hoverMiljoeElements = await page.getByTestId(TestComponentSelectors.HOVER_MILJOE).all()
	for (const element of hoverMiljoeElements) {
		await expect(element).toBeVisible()
		await element.click()

		await page.waitForTimeout(100)
	}

	await page.evaluate((selector) => {
		const element = document.querySelector(`[data-testid="${selector}"]`)
		if (element) {
			const event = new MouseEvent('mouseover', {
				bubbles: true,
				cancelable: true,
			})
			element.dispatchEvent(event)
		}
	}, TestComponentSelectors.TITLE_VISNING)
})

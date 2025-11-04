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

	// Sequential hover to avoid element detachment issues
	const bestillingIkoner = await page
		.getByTestId(TestComponentSelectors.BUTTON_OPEN_BESTILLING)
		.all()
	for (const ikon of bestillingIkoner) {
		try {
			await ikon.hover({ force: true })
			await page.waitForTimeout(50)
		} catch (_) {
			// ignore if element detaches
		}
	}

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

test('skal disable AAREG checkbox ved legg til/endre når Aareg timeout oppstår', async ({
	page,
}) => {
	const AAREG_ROUTE = new RegExp(
		/\/testnav-aareg-proxy\/.*\/api\/v1\/arbeidstaker\/arbeidsforhold.*$/,
	)
	await page.route(AAREG_ROUTE, async (route) => {
		await new Promise((r) => setTimeout(r, 2000))
		await route.abort()
	})
	await page.goto('gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_OPEN_IDENT).click()
	await page.getByText('Laster Aareg-data').waitFor({ timeout: 5000 })

	await expect(page.getByText('Laster Aareg-data')).toHaveCount(0)

	await page.getByText('LEGG TIL/ENDRE').click()
	await page.waitForURL(/\/gruppe\/.*\/bestilling\/.*$/)
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()
	await page.getByText('Arbeid og inntekt').click()

	await expect(page.getByTestId(TestComponentSelectors.CHECKBOX_AAREG)).toBeDisabled()

	// Sjekker at ny ident fortsatt kan opprettes med arbeidsforhold selv om AAREG hadde timeout
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_FINNPERSON).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_PERSONER).click()

	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()
	await page.getByTestId(TestComponentSelectors.BUTTON_OPPRETT_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	await page.getByText('Arbeid og inntekt').click()

	await page.getByTestId(TestComponentSelectors.CHECKBOX_AAREG).waitFor({ timeout: 30000 })
	await expect(page.getByTestId(TestComponentSelectors.CHECKBOX_AAREG)).not.toBeDisabled()
})

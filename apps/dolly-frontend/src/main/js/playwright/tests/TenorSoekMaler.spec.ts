import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

// Min side: Tenor-søk maler skal vises gruppert under overskrifter med formaterte verdier
test('Tenor-søk mal vises gruppert og formatert på Min side', async ({ page }) => {
	await page.goto('')
	await page.getByTestId(TestComponentSelectors.BUTTON_PROFIL).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_PROFIL_MINSIDE).click()

	// Bytt til Tenor-søk-fanen og åpne malen
	await page.getByRole('tab', { name: 'Tenor-søk' }).click()
	const panel = page.getByRole('tabpanel')
	await expect(panel.getByText('Mitt Tenor-søk')).toBeVisible()
	await panel.locator('button[aria-expanded="false"]').first().click()

	// Overskrifter kommer fra navnet på hvert array i soekFormPaths
	await expect(panel.getByRole('heading', { name: 'Identifikasjon og status' })).toBeVisible()
	await expect(panel.getByRole('heading', { name: 'Navn', exact: true })).toBeVisible()
	await expect(panel.getByRole('heading', { name: 'Relasjoner' })).toBeVisible()

	// Verdiene skal være formatert: kode -> label, dato, boolean og array
	await expect(panel.getByText('Mann', { exact: true })).toBeVisible()
	await expect(panel.getByText('01.01.2020')).toBeVisible()
	await expect(panel.getByText('Ja', { exact: true })).toBeVisible()
	await expect(panel.getByText('Far, Mor')).toBeVisible()
})

// Tenor-søk: mal-velgeren skal vises, og man skal kunne opprette en ny mal fra søket
test('Opprett ny mal fra Tenor-søk', async ({ page }) => {
	await page.goto('')
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_FINNPERSON).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_TENOR).click()
	await expect(
		page
			.locator('h1')
			.getByText(/Søk etter personer i Tenor/)
			.first(),
	).toBeVisible()

	// Mal-velgeren fra SoekMalVelger skal være synlig
	await expect(page.getByText('Bruker/team')).toBeVisible()
	await expect(page.getByText('Velg mal for søk')).toBeVisible()

	// Uten søkekriterier skal "Opprett mal fra søk" være deaktivert
	const opprettMalKnapp = page.getByTestId(
		TestComponentSelectors.BUTTON_BESTILLINGDETALJER_OPPRETT_MAL,
	)
	await expect(opprettMalKnapp).toBeDisabled()

	// Legg til et søkekriterium slik at malen kan lagres
	await page.getByTestId(TestComponentSelectors.CHECKBOX_TENORSOEK).click()
	await expect(opprettMalKnapp).toBeEnabled()

	// Åpne dialogen og fyll inn malnavn
	await opprettMalKnapp.click()
	await expect(page.getByRole('heading', { name: 'Opprett mal fra søk' })).toBeVisible()
	const lagreMalKnapp = page.getByRole('button', { name: 'Lagre mal' })
	await expect(lagreMalKnapp).toBeDisabled()
	await page.getByLabel('Navn på mal').fill('Min nye Tenor-søk mal')
	await expect(lagreMalKnapp).toBeEnabled()
})

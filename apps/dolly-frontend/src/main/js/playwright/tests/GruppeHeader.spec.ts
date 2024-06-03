import { expect, test } from '@playwright/test'

import { CypressSelector } from '../../cypress/mocks/Selectors'

test.describe('Testing av forskjellige actions på gruppeheaderen', () => {
	test('Legg til på alle i gruppe', async ({ page }) => {
		await page.goto('gruppe')
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		page.getByTestId(CypressSelector.BUTTON_LEGGTILPAAALLE)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_VIDERE)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_VIDERE)
		await page.click()
		page.getByTestId(CypressSelector.BUTTON_FULLFOER_BESTILLING)
		await page.click()
	})

	const tagsPost = new RegExp(/dolly-backend\/api\/v1\/tags/)
	test('Posting av tags', async ({ page }) => {
		await page.goto('gruppe')
		const postTags = page.waitForResponse({ statusCode: 201 })
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		page.getByTestId(CypressSelector.BUTTON_TILKNYTT_TAGS)
		await page.click()
		await page.locator('.select__input-container').fill('DUMMY')
		await page.locator('.select__input-container').press('Enter')
		page.getByTestId(CypressSelector.BUTTON_POST_TAGS)
		await page.click()
		await expect.poll(async () => (await postTags).status()).toBe(201)
		await expect(
			page
				.locator('h1')
				.getByText(/Testytest/)
				.first(),
		).toBeVisible()
	})

	test('Flyttpersoner funksjonalitet', async ({ page }) => {
		await page.goto('gruppe')
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		page.getByTestId(CypressSelector.BUTTON_FLYTT_PERSONER)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_ALLE_GRUPPER)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_EKSISTERENDE_GRUPPE)
		await page.click()
		page.getByTestId(CypressSelector.TOGGLE_NY_GRUPPE)
		await page.click()
		page.getByTestId(CypressSelector.INPUT_NY_GRUPPE_NAVN)
		await page.fill('TestNavn')
		page.getByTestId(CypressSelector.INPUT_NY_GRUPPE_HENSIKT)
		await page.fill('TestHensikt')
		page.getByTestId(CypressSelector.BUTTON_NY_GRUPPE_OPPRETT)
		await page.click()
		await page
			.locator('.navds-checkbox__label')
			.getByText(/12345678912/)
			.first()
			.click()
		page.getByTestId(CypressSelector.CONTAINER_VALGTE_PERSONER)
		await page.FIXME_should('contain', '12345678912')
		page.getByTestId(CypressSelector.BUTTON_FLYTT_PERSONER_NULLSTILL)
		await page.click()
		page.getByTestId(CypressSelector.CONTAINER_VALGTE_PERSONER)
		await page.FIXME_should('not.contain', '12345678912')
		page.getByTestId(CypressSelector.BUTTON_FLYTT_PERSONER_AVBRYT)
		await page.click()
	})

	test('Gjenopprett gruppe funksjonalitet', async ({ page }) => {
		await page.goto('gruppe')
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		page.getByTestId(CypressSelector.BUTTON_GJENOPPRETT_GRUPPE)
		await page.click()
		await page.locator('#q2').click()
		page.getByTestId(CypressSelector.BUTTON_BESTILLINGDETALJER_GJENOPPRETT_UTFOER)
		await page.click()
	})

	test('Rediger gruppe funksjonalitet', async ({ page }) => {
		await page.goto('gruppe')
		await page
			.locator('div')
			.getByText(/Testytest/)
			.first()
			.click()
		page.getByTestId(CypressSelector.BUTTON_REDIGER_GRUPPE)
		await page.click()
		page.getByTestId(CypressSelector.INPUT_NAVN)
		await page.clear()
		await page.fill('Redigert navn')
		page.getByTestId(CypressSelector.INPUT_HENSIKT)
		await page.clear()
		await page.fill('Redigert hensikt')
		page.getByTestId(CypressSelector.BUTTON_OPPRETT)
		await page.click()
	})
})

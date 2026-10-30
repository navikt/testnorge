import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import {
	backendBestillingerMock,
	brukerMalerMock,
	eksisterendeGruppeMock,
	henvendelseBestillingMock,
	henvendelseListeMock,
	henvendelseMalMock,
	henvendelseTemagrupperMock,
	testidentMock,
} from '#/mocks/BasicMocks'
import { Page } from '@playwright/test'

const hentGruppeEn = new RegExp(/\/api\/v1\/gruppe\/1(?!\/identer)/)
const brukerMaler = new RegExp(/dolly-backend\/api\/v1\/malbestilling\/brukerId/)
const henvendelseTemagrupper = new RegExp(
	/testnav-dolly-proxy\/henvendelse\/api\/henvendelse\/kodeverk\/temagrupper/,
)
const henvendelseListe = new RegExp(
	/testnav-dolly-proxy\/henvendelse\/api\/henvendelseinfo\/henvendelseliste/,
)

const salesforceGruppeMock = {
	...eksisterendeGruppeMock,
	tags: ['SALESFORCE'],
	identer: [
		{
			...testidentMock,
			bestillingId: [...testidentMock.bestillingId, henvendelseBestillingMock.id],
			bestillinger: [...backendBestillingerMock, henvendelseBestillingMock],
		},
	],
}

const fulfillJson = (page: Page, url: RegExp, response: any) =>
	page.route(url, (route) =>
		route.fulfill({
			status: 200,
			body: JSON.stringify(response),
			headers: { 'content-type': 'application/json' },
		}),
	)

const mockHenvendelse = async (page: Page, gruppe = salesforceGruppeMock) => {
	await fulfillJson(page, hentGruppeEn, gruppe)
	await fulfillJson(page, henvendelseTemagrupper, henvendelseTemagrupperMock)
	await fulfillJson(page, henvendelseListe, henvendelseListeMock)
	await fulfillJson(page, brukerMaler, [...brukerMalerMock, henvendelseMalMock])
}

const aapneIdent = async (page: Page) => {
	await page.goto('gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_OPEN_IDENT).click()
}

const hentFormState = async (page: Page) => {
	const label = page.getByText('Vis form')
	if (await label.count()) {
		await label.click()
	}
	const snapshotText = await page.getByTestId(TestComponentSelectors.VALUES_FORM_STATE).innerText()
	const start = snapshotText.indexOf('{')
	const end = snapshotText.lastIndexOf('}')
	return JSON.parse(snapshotText.substring(start, end + 1))
}

const henvendelseMalInfo =
	/Denne malen inneholder henvendelser fra Salesforce, og kan kun brukes ved legg til/

test('viser eksisterende henvendelser i identvisning', async ({ page }) => {
	await mockHenvendelse(page)
	await aapneIdent(page)

	await expect(page.getByText('Henvendelser', { exact: true })).toBeVisible()
	await expect(page.getByText('MELDINGSKJEDE-123')).toBeVisible()
	await expect(page.getByText('SAMTALEREFERAT-456')).toBeVisible()
})

test('viser ikke Salesforce-panel ved ny bestilling', async ({ page }) => {
	await mockHenvendelse(page)
	await page.goto('gruppe/1')
	await page.getByTestId(TestComponentSelectors.BUTTON_OPPRETT_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	await expect(page.getByRole('heading', { name: 'Personinformasjon' })).toBeVisible()
	await expect(page.getByRole('heading', { name: 'Salesforce' })).toHaveCount(0)
})

test('viser ikke Salesforce-panel ved legg til på person i gruppe uten SALESFORCE-tag', async ({
	page,
}) => {
	await mockHenvendelse(page, { ...salesforceGruppeMock, tags: [] })
	await aapneIdent(page)
	await page.getByText('Legg til / endre').click()
	await page.waitForURL(/\/gruppe\/.*\/bestilling\/.*$/)
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	await expect(page.getByRole('heading', { name: 'Personinformasjon' })).toBeVisible()
	await expect(page.getByRole('heading', { name: 'Salesforce' })).toHaveCount(0)
})

test('legg til henvendelse på person i gruppe med SALESFORCE-tag', async ({ page }) => {
	await mockHenvendelse(page)
	await aapneIdent(page)
	await page.getByText('Legg til / endre').click()
	await page.waitForURL(/\/gruppe\/.*\/bestilling\/.*$/)
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	await page.getByRole('heading', { name: 'Salesforce' }).click()
	await page.getByLabel('Har meldinger').check()
	await page.getByLabel('Har samtalereferater').check()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	await page
		.getByRole('region', { name: 'Eksisterende henvendelser på person' })
		.getByRole('button', { name: 'Vis mer' })
		.click()
	await expect(page.getByText('MELDINGSKJEDE-123').first()).toBeVisible()

	const meldingKjedeId = page.locator('[id="henvendelse.meldinger.0.kjedeId"]')
	const samtalereferatKjedeId = page.locator('[id="henvendelse.samtalereferater.0.kjedeId"]')
	const aapenMeny = page.locator('.select__menu')
	const meldingskjedeOption = aapenMeny.getByText('MELDINGSKJEDE-123 (01.10.2026 10:00)')
	const samtalereferatOption = aapenMeny.getByText('SAMTALEREFERAT-456 (02.10.2026 12:00)')

	await meldingKjedeId.click()
	await expect(meldingskjedeOption).toBeVisible()
	await expect(samtalereferatOption).toHaveCount(0)
	await meldingskjedeOption.click()

	await samtalereferatKjedeId.click()
	await expect(samtalereferatOption).toBeVisible()
	await expect(meldingskjedeOption).toHaveCount(0)
	await samtalereferatOption.click()

	const formState = await hentFormState(page)
	expect(formState.henvendelse.meldinger[0].kjedeId).toBe('MELDINGSKJEDE-123')
	expect(formState.henvendelse.samtalereferater[0].kjedeId).toBe('SAMTALEREFERAT-456')
})

test('kjede-ID er deaktivert når person ikke har eksisterende henvendelser', async ({ page }) => {
	await mockHenvendelse(page)
	await fulfillJson(page, henvendelseListe, { ...henvendelseListeMock, data: [] })
	await aapneIdent(page)
	await page.getByText('Legg til / endre').click()
	await page.waitForURL(/\/gruppe\/.*\/bestilling\/.*$/)
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	await page.getByRole('heading', { name: 'Salesforce' }).click()
	await page.getByLabel('Har meldinger').check()
	await page.getByTestId(TestComponentSelectors.BUTTON_VIDERE).click()

	await expect(page.getByRole('heading', { name: 'Henvendelse' })).toBeVisible()
	await expect(page.getByText('Eksisterende henvendelser på person')).toHaveCount(0)
	await expect(page.locator('[id="henvendelse.meldinger.0.kjedeId"]')).toBeDisabled()
})

test('mal med henvendelse fjerner henvendelse ved ny bestilling', async ({ page }) => {
	await mockHenvendelse(page)
	await page.goto('gruppe/1')
	await page.getByTestId(TestComponentSelectors.BUTTON_OPPRETT_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_MAL).click()
	await page.getByTestId(TestComponentSelectors.SELECT_MAL).click()
	await page.getByText('Henvendelse mal').click()

	await expect(page.getByText(henvendelseMalInfo)).toBeVisible()
	const formState = await hentFormState(page)
	expect(formState.henvendelse).toBeUndefined()
})

test('mal med henvendelse beholder henvendelse ved legg til i gruppe med SALESFORCE-tag', async ({
	page,
}) => {
	await mockHenvendelse(page)
	await aapneIdent(page)
	await page.getByText('Legg til / endre').click()
	await page.waitForURL(/\/gruppe\/.*\/bestilling\/.*$/)
	await page.getByTestId(TestComponentSelectors.TOGGLE_MAL).click()
	await page.getByTestId(TestComponentSelectors.SELECT_MAL).click()
	await page.getByText('Henvendelse mal').click()

	await expect(page.getByText(henvendelseMalInfo)).toHaveCount(0)
	const formState = await hentFormState(page)
	expect(formState.henvendelse.meldinger[0]).toMatchObject({
		temagruppe: 'ARBD',
		fritekst: 'Melding fra mal',
		tema: null,
		enhet: null,
		kjedeId: null,
		tildelMeg: false,
	})
})

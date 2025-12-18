import { expect, test } from '#/globalSetup'
import { personOrgTilgangMock } from '#/mocks/BasicMocks'
import { TestComponentSelectors } from '#/mocks/Selectors'

const mockOrganisasjon = {
	navn: 'Test Organisasjon',
	organisasjonsnummer: '123456789',
	organisasjonform: 'AS',
}

const mockBankidBruker = {
	brukernavn: 'bankid-user',
	brukertype: 'BANKID',
	organisasjonsnummer: null,
}

test.beforeEach(async ({ page }) => {
	await page.route('**/api/v1/bruker/current', async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify(mockBankidBruker),
		})
	})

	await page.route('**/testnorge-profil-api/api/v1/profil', async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify({}),
		})
	})

	await page.route('**/person-org-tilgang-api/api/v1/organisasjoner', async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify([mockOrganisasjon]),
		})
	})

	await page.route('**/altinn/organisasjoner', async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify(personOrgTilgangMock),
		})
	})

	await page.route('**/session/user', async (route) => {
		if (route.request().method() === 'PUT') {
			await route.fulfill({ status: 200 })
		} else {
			await route.continue()
		}
	})
})

test('should handle new user login flow through BrukerModal', async ({ page }) => {
	await page.route(
		`**/testnav-bruker-service/api/v1/brukere?organisasjonsnummer=${mockOrganisasjon.organisasjonsnummer}`,
		async (route) => {
			await route.fulfill({
				status: 404,
			})
		},
	)

	await page.route('**/testnav-bruker-service/api/v2/brukere', async (route) => {
		if (route.request().method() === 'POST') {
			const body = route.request().postDataJSON()
			await route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: JSON.stringify({
					brukernavn: body.brukernavn,
					epost: body.epost,
				}),
			})
		} else {
			await route.continue()
		}
	})

	await page.goto('/bruker')

	await page.getByTestId(TestComponentSelectors.BUTTON_VELG_ORG_BRUKER).click()

	await expect(page.getByText(/Fyll inn eget/)).toBeVisible()
	await page.getByLabel('Navn').fill('Ny Bruker 123')
	await page.getByLabel('Epost').fill('test@test.com')
	await page.getByRole('button', { name: 'Gå videre til Dolly' }).click()

	await page.waitForURL('/')
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_FINNPERSON).click()
	await expect(page.getByRole('link', { name: 'Mine personer' })).toBeVisible()
})

test('should handle existing user without email', async ({ page }) => {
	const mockExistingUserWithoutEmail = {
		brukernavn: 'existing user',
		epost: null,
		organisasjonsnummer: mockOrganisasjon.organisasjonsnummer,
	}

	await page.route(
		`**/testnav-bruker-service/api/v2/brukere?organisasjonsnummer=12345678`,
		async (route) => {
			await route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: JSON.stringify([mockExistingUserWithoutEmail]),
			})
		},
	)

	await page.route('**/testnav-bruker-service/api/v2/brukere', async (route) => {
		if (route.request().method() === 'PUT') {
			const body = route.request().postDataJSON()
			await route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: JSON.stringify({
					brukernavn: body.brukernavn,
					epost: body.epost,
				}),
			})
		} else {
			await route.continue()
		}
	})

	await page.goto('/bruker')

	await page.getByTestId(TestComponentSelectors.BUTTON_VELG_ORG_BRUKER).click()

	await expect(page.getByText(/Fyll inn eget/)).toBeVisible()
	const navnInput = page.getByLabel('Navn')
	await expect(navnInput).toBeDisabled()
	await expect(navnInput).toHaveValue(mockExistingUserWithoutEmail.brukernavn)

	await page.getByLabel('Epost').fill('update@test.com')
	await page.getByRole('button', { name: 'Gå videre til Dolly' }).click()

	await page.waitForURL('/')
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_FINNPERSON).click()
	await expect(page.getByRole('link', { name: 'Mine personer' })).toBeVisible()
})

test('should handle new BankID user flow', async ({ page }) => {
	const orgNummer = '12345678'
	const brukernavn = 'testbruker123'
	const epost = 'test@test.com'

	await page.route('**/api/v1/organisasjoner', async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify([
				{
					navn: 'Test Organisasjon',
					organisasjonsnummer: orgNummer,
					organisasjonsform: 'AS',
				},
			]),
		})
	})

	await page.route(
		`**/testnav-bruker-service/api/v2/brukere?organisasjonsnummer=${orgNummer}`,
		async (route) => {
			await route.fulfill({
				status: 404,
			})
		},
	)

	await page.route('**/testnav-bruker-service/api/v2/brukere', async (route) => {
		if (route.request().method() === 'POST') {
			const body = route.request().postDataJSON()
			expect(body.brukernavn).toBe(brukernavn)
			expect(body.epost).toBe(epost)
			expect(body.organisasjonsnummer).toBe(orgNummer)
			await route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: JSON.stringify({
					brukernavn: brukernavn,
					epost: epost,
				}),
			})
		} else {
			await route.continue()
		}
	})

	await page.route('**/session/organisasjon', async (route) => {
		if (route.request().method() === 'POST') {
			const body = route.request().postDataJSON()
			expect(body.organisasjonsnummer).toBe(orgNummer)
			await route.fulfill({ status: 200 })
		} else {
			await route.continue()
		}
	})

	await page.goto('/bruker')
	await page.getByTestId(TestComponentSelectors.BUTTON_VELG_ORG_BRUKER).click()

	await expect(page.getByText(/Fyll inn eget navn/)).toBeVisible()

	await page.getByLabel('Navn').fill(brukernavn)
	await page.getByLabel('Epost').fill(epost)

	await page.getByRole('button', { name: 'Gå videre til Dolly' }).click()

	await expect(page).toHaveURL('/gruppe')
})

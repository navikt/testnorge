import { expect, test } from '@playwright/experimental-ct-react'
import BrukernavnVelger from '@/pages/brukerPage/BrukernavnVelger'
import { Organisasjon } from '@/pages/brukerPage/types'

const mockOrganisasjon: Organisasjon = {
	navn: 'Test Organisasjon',
	organisasjonsnummer: '123456789',
}

test.use({ viewport: { width: 800, height: 600 } })

test('should allow manual interaction with BrukernavnVelger', async ({ mount, page }) => {
	let sessionOrg = ''

	// Mock the API call to check for username availability
	await page.route(
		'/testnav-bruker-service/api/v1/brukere/brukernavn/testbruker123',
		async (route) => {
			// Fulfill with status 200 and a body of 404 to simulate the service's behavior
			await route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: '404',
			})
		},
	)

	// Mock the API call to create a new user
	await page.route('/testnav-bruker-service/api/v1/brukere', async (route) => {
		if (route.request().method() === 'POST') {
			await route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: JSON.stringify({
					brukernavn: 'testbruker123',
					epost: 'test@test.com',
				}),
			})
		}
	})

	// Mount the component with props
	const component = await mount(
		<BrukernavnVelger
			organisasjon={mockOrganisasjon}
			addToSession={(org) => {
				sessionOrg = org
			}}
		/>,
	)

	await component.getByLabel('Brukernavn').fill('testbruker123')
	await component.getByLabel('Epost').fill('test@test.com')

	const submitButton = component.getByRole('button', { name: 'Gå videre til Dolly' })

	await expect(submitButton).toBeEnabled()

	await submitButton.click()

	await page.pause()
})

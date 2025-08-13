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

	await page.route(
		'/testnav-bruker-service/api/v2/brukere/brukernavn/testbruker123',
		async (route) => {
			await route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: '404',
			})
		},
	)

	await page.route('/testnav-bruker-service/api/v2/brukere', async (route) => {
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
})

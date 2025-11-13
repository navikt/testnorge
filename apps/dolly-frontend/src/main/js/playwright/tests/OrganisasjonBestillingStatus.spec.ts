import { expect, test } from '#/globalSetup'

const organisasjonBestillingResponse = {
	id: 1,
	antallLevert: 1,
	ferdig: false,
	sistOppdatert: '2025-11-10T10:31:38.707672',
	environments: ['q1', 'q2'],
	organisasjonNummer: '123456789',
	status: [
		{
			id: 'ORGANISASJON_FORVALTER',
			statuser: [
				{
					melding: 'OK',
					detaljert: [
						{ miljo: 'q1', orgnummer: '123456789' },
						{ miljo: 'q2', orgnummer: '123456789' },
					],
				},
			],
			navn: 'Enhetsregisteret (EREG)',
		},
	],
	bestilling: {
		enhetstype: 'AS',
		stiftelsesdato: '2025-11-06',
		underenheter: [{ enhetstype: 'BEDR', stiftelsesdato: '2025-11-10' }],
	},
	systeminfo: '',
}

test('Vis organisasjon bestilling status', async ({ page }) => {
	await page.route(/organisasjon\/bestilling\?bestillingId=1/, (route) => {
		route.fulfill({ body: JSON.stringify(organisasjonBestillingResponse) })
	})
	await page.route(/organisasjon\/bestilling\/bestillingsstatus/, (route) => {
		route.fulfill({ body: JSON.stringify([organisasjonBestillingResponse]) })
	})
	await page.goto('/organisasjoner')
	await page.getByText('Bestilling #1').waitFor({ state: 'visible' })
	await expect(page.getByText('Enhetsregisteret (EREG)')).toBeVisible()
	await expect(page.getByText(/123456789/)).toBeVisible()
})

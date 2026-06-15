import { expect, test } from '@playwright/experimental-ct-react'
import { HendelseIdDataVisning } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/HendelseIdDataVisning'
import { Provider } from 'react-redux'
import { configureStore } from '@reduxjs/toolkit'

const HENDELSE_ROUTE = /dolly-backend\/api\/v1\/hendelseid\/ident\//

const mainPersonResponse = {
	hovedperson: {
		ordrer: [
			{
				infoElement: 'PDL_SLETTING',
				hendelser: [{ status: 'OK' }],
			},
			{
				infoElement: 'PDL_OPPRETT_PERSON',
				hendelser: [{ status: 'OK' }],
			},
			{
				infoElement: 'PDL_DOEDSFALL',
				hendelser: [{ id: 1, status: 'OK', hendelseId: 'abc-123' }],
			},
			{
				infoElement: 'PDL_NAVN',
				hendelser: [
					{ id: 2, status: 'OK', hendelseId: 'def-456' },
					{ id: 3, status: 'OK', hendelseId: 'ghi-789' },
				],
			},
			{
				infoElement: 'PDL_SIVILSTAND',
				hendelser: [{ id: 1, status: 'OK', hendelseId: 'siv-1' }],
			},
			{
				infoElement: 'PDL_SIVILSTAND',
				hendelser: [{ id: 2, status: 'OK', hendelseId: 'siv-2' }],
			},
		],
	},
	relasjoner: [],
}

const feilPersonResponse = {
	hovedperson: {
		ordrer: [
			{
				infoElement: 'PDL_SLETTING',
				hendelser: [{ status: 'FEIL', error: 'Mottaker svarer ikke, eller har for lang svartid.' }],
			},
		],
	},
	relasjoner: [],
}

const relatertPersonResponse = {
	ident: '11111111111',
	ordrer: [
		{
			infoElement: 'PDL_FORELDRE_BARN_RELASJON',
			hendelser: [{ id: 7, status: 'OK', hendelseId: 'rel-789' }],
		},
	],
}

const importertRelasjonResponse = {
	ident: '11111111111',
	ordrer: [],
}

test.use({ viewport: { width: 1200, height: 800 } })

const testStore = configureStore({
	reducer: () => ({}),
})

test('should render ident trigger button', async ({ mount }) => {
	const component = await mount(<HendelseIdDataVisning ident="12345678901" />)
	await expect(component.getByText('12345678901 (HOVEDPERSON)')).toBeVisible()
})

test('should render trigger buttons for related persons', async ({ mount }) => {
	const component = await mount(
		<HendelseIdDataVisning
			ident="12345678901"
			relatertePersoner={[
				{ type: 'BARN', id: '11111111111' },
				{ type: 'PARTNER', id: '22222222222' },
			]}
		/>,
	)
	await expect(component.getByText('12345678901 (HOVEDPERSON)')).toBeVisible()
	await expect(component.getByText('11111111111 (BARN)')).toBeVisible()
	await expect(component.getByText('22222222222 (PARTNER)')).toBeVisible()
})

test('should show one accordion category per infoElement on hover', async ({ mount, page }) => {
	await page.route(HENDELSE_ROUTE, async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify(mainPersonResponse),
		})
	})

	const component = await mount(<HendelseIdDataVisning ident="12345678901" />)

	await component.getByText('12345678901 (HOVEDPERSON)').hover({ force: true })
	await expect(page.getByRole('button', { name: /dødsfall/i })).toBeVisible()
	await expect(page.getByRole('button', { name: /navn/i })).toBeVisible()
	await expect(page.getByRole('button', { name: /sivilstand/i })).toBeVisible()

	await page.getByRole('button', { name: /dødsfall/i }).click()
	await expect(page.getByText('abc-123')).toBeVisible()
})

test('should hide noise events without errors', async ({ mount, page }) => {
	await page.route(HENDELSE_ROUTE, async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify(mainPersonResponse),
		})
	})

	const component = await mount(<HendelseIdDataVisning ident="12345678901" />)

	await component.getByText('12345678901 (HOVEDPERSON)').hover({ force: true })
	await expect(page.getByRole('button', { name: /dødsfall/i })).toBeVisible()
	await expect(page.getByRole('button', { name: /sletting/i })).toBeHidden()
	await expect(page.getByRole('button', { name: /opprett/i })).toBeHidden()
})

test('should close on hover leave before any category is opened', async ({ mount, page }) => {
	await page.route(HENDELSE_ROUTE, async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify(mainPersonResponse),
		})
	})

	const component = await mount(<HendelseIdDataVisning ident="12345678901" />)

	await component.getByText('12345678901 (HOVEDPERSON)').hover({ force: true })
	await expect(page.getByRole('button', { name: /dødsfall/i })).toBeVisible()

	await page.mouse.move(0, 0)
	await expect(page.getByRole('button', { name: /dødsfall/i })).toBeHidden()
})

test('should show a copy button per hendelse inside the same category', async ({ mount, page }) => {
	await page.route(HENDELSE_ROUTE, async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify(mainPersonResponse),
		})
	})

	const component = await mount(<HendelseIdDataVisning ident="12345678901" />)

	await component.getByText('12345678901 (HOVEDPERSON)').hover({ force: true })
	await page.waitForTimeout(300)

	await page.getByRole('button', { name: /navn/i }).first().click()
	await expect(page.getByText('def-456')).toBeVisible()
	await expect(page.getByText('ghi-789')).toBeVisible()
	const navnItem = page.locator('.aksel-accordion__item', {
		has: page.getByRole('button', { name: /navn/i }),
	})
	await expect(navnItem.locator('.aksel-copybutton')).toHaveCount(2)
})

test('should colour the category red and show the error message on failure', async ({
	mount,
	page,
}) => {
	await page.route(HENDELSE_ROUTE, async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify(feilPersonResponse),
		})
	})

	const component = await mount(<HendelseIdDataVisning ident="12345678901" />)

	await component.getByText('12345678901 (HOVEDPERSON)').hover({ force: true })
	await expect(page.locator('.hendelse-kategori-feil')).toBeVisible()

	await page.getByRole('button', { name: /sletting/i }).click()
	await expect(page.getByText('Mottaker svarer ikke, eller har for lang svartid.')).toBeVisible()
})

test('should show imported-relation warning immediately and hide related hover button', async ({
	mount,
	page,
}) => {
	await page.route(HENDELSE_ROUTE, async (route) => {
		const url = route.request().url()
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify(
				url.includes('relatertIdent=11111111111') ? importertRelasjonResponse : mainPersonResponse,
			),
		})
	})

	const component = await mount(
		<Provider store={testStore}>
			<HendelseIdDataVisning
				ident="12345678901"
				relatertePersoner={[{ type: 'BARN', id: '11111111111' }]}
			/>
		</Provider>,
	)

	await expect(page.getByText(/importert/i)).toBeVisible()
	await expect(page.getByText(/hovedperson/i)).toBeVisible()
	await expect(component.getByRole('button', { name: '11111111111 (BARN)' })).toHaveCount(0)
	await expect(component.getByRole('button', { name: '12345678901 (HOVEDPERSON)' })).toHaveCount(0)
})

test('should stay open after category interaction and close on click', async ({ mount, page }) => {
	await page.route(HENDELSE_ROUTE, async (route) => {
		await route.fulfill({
			status: 200,
			contentType: 'application/json',
			body: JSON.stringify(mainPersonResponse),
		})
	})

	const component = await mount(<HendelseIdDataVisning ident="12345678901" />)

	await component.getByText('12345678901 (HOVEDPERSON)').hover({ force: true })
	await page
		.getByRole('button', { name: /sivilstand/i })
		.first()
		.click()
	await expect(page.getByText('siv-1')).toBeVisible()

	await page.mouse.move(0, 0)
	await expect(page.getByText('siv-1')).toBeVisible()

	await component.getByText('12345678901 (HOVEDPERSON)').click({ force: true })
	await expect(page.getByText('siv-1')).toBeHidden()
})

test('should request ordrer for relatert ident with correct query param', async ({
	mount,
	page,
}) => {
	const requestedUrls: string[] = []
	await page.route(HENDELSE_ROUTE, async (route) => {
		const requestedUrl = route.request().url()
		requestedUrls.push(requestedUrl)
		if (requestedUrl.includes('relatertIdent=11111111111')) {
			await route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: JSON.stringify(relatertPersonResponse),
			})
		} else {
			await route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: JSON.stringify(mainPersonResponse),
			})
		}
	})

	const component = await mount(
		<HendelseIdDataVisning
			ident="12345678901"
			relatertePersoner={[{ type: 'BARN', id: '11111111111' }]}
		/>,
	)
	await component.getByText('11111111111 (BARN)').hover({ force: true })
	await page.waitForTimeout(300)

	await expect
		.poll(() => requestedUrls.some((url) => url.includes('relatertIdent=11111111111')))
		.toBe(true)
})

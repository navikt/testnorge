import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

const base = /dolly-backend\/api\/v1\/infostriper.*/
type Infostripe = {
	id: number
	message: string
	start: string
	end: string
	aktiv: boolean
}

//TODO: Fikse feilende tester og sørge for at denne kjører som tiltenkt, denne testen kan fjernes hvis det blir for mye pes

const initialData: Infostripe[] = [
	{
		id: 1,
		message: 'Eksisterende infostripe',
		start: '2030-01-10T08:00:00Z',
		end: '2030-01-20T22:00:00Z',
		aktiv: true,
	},
	{
		id: 2,
		message: 'Nyere infostripe (skal vises først etter sortering)',
		start: '2030-02-01T08:00:00Z',
		end: '2030-02-05T22:00:00Z',
		aktiv: true,
	},
]

test.describe('DollyInfostripePage CRUD', () => {
	test.beforeEach(async ({ page }) => {
		const state: Infostripe[] = JSON.parse(JSON.stringify(initialData))
		let nextId = 100

		// GET
		await page.route(base, async (route) => {
			if (route.request().method() === 'GET') {
				return route.fulfill({ status: 200, body: JSON.stringify(state) })
			}
			await route.fallback()
		})

		// POST
		await page.route(base, async (route) => {
			if (route.request().method() === 'POST') {
				const req = await route.request().postDataJSON()
				const newStripe: Infostripe = {
					id: nextId++,
					message: req.message ?? 'Ukjent',
					start: req.start,
					end: req.end,
					aktiv: true,
				}
				state.push(newStripe)
				return route.fulfill({ status: 201, body: JSON.stringify(newStripe) })
			}
			await route.fallback()
		})

		// PUT
		await page.route(/dolly-backend\/api\/v1\/infostriper\/\d+$/, async (route) => {
			if (route.request().method() === 'PUT') {
				const id = Number(
					route
						.request()
						.url()
						.match(/(\d+)$/)![1],
				)
				const req = await route.request().postDataJSON()
				const idx = state.findIndex((s) => s.id === id)
				if (idx >= 0) {
					state[idx] = { ...state[idx], ...req }
					return route.fulfill({ status: 200, body: JSON.stringify(state[idx]) })
				}
				return route.fulfill({ status: 404 })
			}
			await route.fallback()
		})

		// DELETE
		await page.route(/dolly-backend\/api\/v1\/infostriper\/\d+$/, async (route) => {
			if (route.request().method() === 'DELETE') {
				const id = Number(
					route
						.request()
						.url()
						.match(/(\d+)$/)![1],
				)
				const idx = state.findIndex((s) => s.id === id)
				if (idx >= 0) {
					state.splice(idx, 1)
					return route.fulfill({ status: 204, body: '' })
				}
				return route.fulfill({ status: 404 })
			}
			await route.fallback()
		})

		await page.goto('admin/infostriper')
	})

	test('lists infostriper sorted descending by start date', async ({ page }) => {
		const items = page.getByTestId(TestComponentSelectors.INFOSTRIPE_ITEM)
		await expect(items).toHaveCount(2)
		await expect(items.nth(0)).toContainText('Nyere infostripe')
		await expect(items.nth(1)).toContainText('Eksisterende infostripe')
	})

	test('creates a new infostripe and list resorted', async ({ page }) => {
		const message = 'Helt ny stripe'
		await page.getByTestId(TestComponentSelectors.INPUT_INFOSTRIPE_MESSAGE).fill(message)
		await page.getByTestId(TestComponentSelectors.INPUT_INFOSTRIPE_START).fill('2030-03-01T08:00')
		await page.getByTestId(TestComponentSelectors.INPUT_INFOSTRIPE_END).fill('2030-03-02T20:00')
		await page.getByTestId(TestComponentSelectors.BUTTON_LAGRE_INFOSTRIPE).click()

		const items = page.getByTestId(TestComponentSelectors.INFOSTRIPE_ITEM)
		await expect(items.first()).toContainText(message)
		await expect(items).toHaveCount(3)
	})

	test('edits an existing infostripe', async ({ page }) => {
		const target = page
			.getByTestId(TestComponentSelectors.INFOSTRIPE_ITEM)
			.filter({ hasText: 'Eksisterende infostripe' })
		await target.getByTestId(TestComponentSelectors.BUTTON_EDIT_INFOSTRIPE).click()
		const editInput = target.getByTestId(TestComponentSelectors.INPUT_INFOSTRIPE_MESSAGE_EDIT)
		await editInput.fill('Oppdatert melding')
		await target.getByTestId(TestComponentSelectors.BUTTON_SAVE_INFOSTRIPE).click()
		await expect(target).toContainText('Oppdatert melding')
	})

	test('cancels edit retains original message', async ({ page }) => {
		const target = page
			.getByTestId(TestComponentSelectors.INFOSTRIPE_ITEM)
			.filter({ hasText: 'Eksisterende infostripe' })
		await target.getByTestId(TestComponentSelectors.BUTTON_EDIT_INFOSTRIPE).click()
		const editInput = target.getByTestId(TestComponentSelectors.INPUT_INFOSTRIPE_MESSAGE_EDIT)
		await editInput.fill('Skal ikke lagres')
		await target.getByTestId(TestComponentSelectors.BUTTON_CANCEL_EDIT_INFOSTRIPE).click()
		await expect(target).not.toContainText('Skal ikke lagres')
		await expect(target).toContainText('Eksisterende infostripe')
	})

	test('deletes an infostripe', async ({ page }) => {
		const toDelete = page
			.getByTestId(TestComponentSelectors.INFOSTRIPE_ITEM)
			.filter({ hasText: 'Nyere infostripe' })
		await toDelete.getByTestId(TestComponentSelectors.BUTTON_DELETE_INFOSTRIPE).click()
		await page.getByTestId(TestComponentSelectors.DIALOG_CONFIRM_SLETTE).waitFor()
		await page.getByTestId(TestComponentSelectors.BUTTON_CONFIRM_SLETTE).click()
		await expect(
			page
				.getByTestId(TestComponentSelectors.INFOSTRIPE_ITEM)
				.filter({ hasText: 'Nyere infostripe' }),
		).toHaveCount(0)
	})

	test('delete cancel leaves item intact', async ({ page }) => {
		const toDelete = page
			.getByTestId(TestComponentSelectors.INFOSTRIPE_ITEM)
			.filter({ hasText: 'Eksisterende infostripe' })
		await toDelete.getByTestId(TestComponentSelectors.BUTTON_DELETE_INFOSTRIPE).click()
		await page.getByTestId(TestComponentSelectors.BUTTON_AVBRYT_SLETTE).click()
		await expect(
			page
				.getByTestId(TestComponentSelectors.INFOSTRIPE_ITEM)
				.filter({ hasText: 'Eksisterende infostripe' }),
		).toHaveCount(1)
	})

	test('handles empty list after deletions', async ({ page }) => {
		for (const text of ['Nyere infostripe', 'Eksisterende infostripe']) {
			const item = page
				.getByTestId(TestComponentSelectors.INFOSTRIPE_ITEM)
				.filter({ hasText: text })
			if (await item.count()) {
				await item.getByTestId(TestComponentSelectors.BUTTON_DELETE_INFOSTRIPE).click()
				await page.getByTestId(TestComponentSelectors.BUTTON_CONFIRM_SLETTE).click()
			}
		}
		await expect(page.getByTestId(TestComponentSelectors.INFOSTRIPE_ITEM)).toHaveCount(0)
	})
})

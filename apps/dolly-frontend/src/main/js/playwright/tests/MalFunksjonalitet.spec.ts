import { expect, test } from '#/globalSetup'
import { TestComponentSelectors } from '#/mocks/Selectors'

const openFormState = async (page: any) => {
	const label = page.getByText('Vis form')
	if (await label.count()) {
		await label.click()
	}
}

const extractJson = (snapshotText: string) => {
	const start = snapshotText.indexOf('{')
	const end = snapshotText.lastIndexOf('}')
	if (start === -1 || end === -1) return {}
	let jsonSegment = snapshotText.substring(start, end + 1)
	// Remove escaping of quotes produced in innerText
	jsonSegment = jsonSegment.replace(/\\"/g, '"')
	try {
		return JSON.parse(jsonSegment)
	} catch (_) {
		return {}
	}
}

const assertSnapshotState = async (page: any, shouldHaveIdenttype: boolean) => {
	await openFormState(page)
	const snapshotText = await page.getByTestId(TestComponentSelectors.VALUES_FORM_STATE).innerText()
	const data = extractJson(snapshotText)
	const identtype = data?.pdldata?.opprettNyPerson?.identtype
	if (shouldHaveIdenttype) {
		expect(identtype).toBeTruthy()
	} else {
		expect(data?.pdldata?.opprettNyPerson).toBeUndefined()
	}
}

// Ny bestilling fra mal skal beholde opprettNyPerson
test('mal pdldata ny bestilling beholder opprettNyPerson', async ({ page }) => {
	await page.goto('gruppe/1')
	await page.getByTestId(TestComponentSelectors.BUTTON_OPPRETT_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_MAL).click()
	await page.getByTestId(TestComponentSelectors.SELECT_MAL).click()
	await page.getByText('Teste Playwright').click()
	await assertSnapshotState(page, true)
})

// Legg til på gruppe (bruker gruppeheader knapp) skal filtrere bort opprettNyPerson
test('mal pdldata leggTilPaaGruppe ekskluderer opprettNyPerson', async ({ page }) => {
	await page.goto('gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()
	await page.getByTestId(TestComponentSelectors.BUTTON_LEGGTILPAAALLE).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_MAL).click()
	await page.getByTestId(TestComponentSelectors.SELECT_MAL).click()
	await page.getByText('Teste Playwright').click()
	await assertSnapshotState(page, false)
})

// Legg til på person (fra identvisning) skal filtrere bort opprettNyPerson
test('mal pdldata leggTil på person ekskluderer opprettNyPerson', async ({ page }) => {
	await page.goto('gruppe')
	await page
		.locator('div')
		.getByText(/Testytest/)
		.first()
		.click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_VISNING_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_OPEN_IDENT).click()
	await page.getByText('LEGG TIL/ENDRE').click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_MAL).click()
	await page.getByTestId(TestComponentSelectors.SELECT_MAL).click()
	await page.getByText('Teste Playwright').click()
	await assertSnapshotState(page, false)
})

// Import personer (Tenor) skal filtrere bort opprettNyPerson
test('mal pdldata importTestnorge ekskluderer opprettNyPerson', async ({ page }) => {
	await page.goto('')
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_FINNPERSON).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_HEADER_TENOR).click()
	await page.getByTestId(TestComponentSelectors.BUTTON_IMPORTER_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_MAL).click()
	await page.getByTestId(TestComponentSelectors.SELECT_MAL).click()
	await page.getByText('Teste Playwright').click()
	await assertSnapshotState(page, false)
})

// Opprett fra identer: toggle eksisterende person, then mal, should exclude opprettNyPerson
test('mal pdldata opprettFraIdenter ekskluderer opprettNyPerson', async ({ page }) => {
	await page.goto('gruppe/1')
	await page.getByTestId(TestComponentSelectors.BUTTON_OPPRETT_PERSONER).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_EKSISTERENDE_PERSON).click()
	await page.getByTestId(TestComponentSelectors.TOGGLE_MAL).click()
	await page.getByTestId(TestComponentSelectors.SELECT_MAL).click()
	await page.getByText('Teste Playwright').click()
	await assertSnapshotState(page, false)
})

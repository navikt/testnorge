import { render, screen, waitFor } from '@testing-library/react'
import { dollyTest } from '../vitest.setup'
import { userEvent } from '@vitest/browser/context'
import { act } from 'react'
import FinnPersonBestilling from '@/pages/gruppeOversikt/FinnPersonBestilling'
import { TestComponentSelectors } from '#/mocks/Selectors'
import createTestStore from './mocks/testStore'
import { BrowserRouter } from 'react-router'
import { Provider } from 'react-redux'

// Mock for navigator.platform to test both Mac and Windows scenarios
const mockNavigator = (platform) => {
	Object.defineProperty(window.navigator, 'platform', {
		value: platform,
		configurable: true,
	})
}

dollyTest('focuses search input when hotkey is pressed', async () => {
	const store = createTestStore()

	render(
		<Provider store={store}>
			<BrowserRouter>
				<FinnPersonBestilling />
			</BrowserRouter>
		</Provider>,
	)

	await waitFor(
		() =>
			expect(
				screen.getByTestId(TestComponentSelectors.CONTAINER_FINN_PERSON_BESTILLING),
			).toBeInTheDocument(),
		{ timeout: 2000 },
	)

	// Find the input by its role instead of data-testid
	const searchInput = screen.getByRole('combobox')
	expect(searchInput).toBeInTheDocument()

	// Test Mac hotkey (⌘+K)
	mockNavigator('MacIntel')

	await act(async () => {
		await userEvent.keyboard('{Meta>}k{/Meta}')
	})

	// Check if search input is focused
	await waitFor(() => expect(document.activeElement).toBe(searchInput), {
		timeout: 1000,
	})

	// Clear focus
	document.body.focus()

	// Test Windows hotkey (Ctrl+K)
	mockNavigator('Win32')

	await act(async () => {
		await userEvent.keyboard('{Control>}k{/Control}')
	})

	// Check if search input is focused again
	await waitFor(() => expect(document.activeElement).toBe(searchInput), {
		timeout: 1000,
	})
})

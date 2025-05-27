import { render, screen, waitFor } from '@testing-library/react'
import { dollyTest } from '../vitest.setup'
import { act } from 'react'
import FinnPersonBestilling from '@/pages/gruppeOversikt/FinnPersonBestilling'
import { TestComponentSelectors } from '#/mocks/Selectors'
import createTestStore from './mocks/testStore'
import { BrowserRouter } from 'react-router'
import { Provider } from 'react-redux'

// Mock for navigator.platform to test both Mac and Windows scenarios
const mockNavigator = (platform: string) => {
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

	const searchInput = screen.getByRole('combobox', { name: '' })
	expect(searchInput).toBeInTheDocument()

	mockNavigator('MacIntel')

	await act(async () => {
		const metaKEvent = new KeyboardEvent('keydown', {
			key: 'k',
			metaKey: true,
			bubbles: true,
		})
		document.dispatchEvent(metaKEvent)
	})

	await waitFor(
		() => {
			expect(document.activeElement?.getAttribute('role')).toBe('combobox')
		},
		{ timeout: 1000 },
	)

	document.body.focus()

	mockNavigator('Win32')

	await act(async () => {
		const ctrlKEvent = new KeyboardEvent('keydown', {
			key: 'k',
			ctrlKey: true,
			bubbles: true,
		})
		document.dispatchEvent(ctrlKEvent)
	})

	await waitFor(
		() => {
			expect(document.activeElement?.getAttribute('role')).toBe('combobox')
		},
		{ timeout: 1000 },
	)
})

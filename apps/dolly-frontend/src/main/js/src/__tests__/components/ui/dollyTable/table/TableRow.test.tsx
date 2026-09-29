import { render, screen, waitFor } from '@testing-library/react'
import { afterAll, beforeAll, beforeEach, describe, expect, it, vi } from 'vitest'
import TableRow from '@/components/ui/dollyTable/table/TableRow'

const mocks = vi.hoisted(() => ({
	dispatch: vi.fn(),
	scrollIntoView: vi.fn(),
}))

vi.mock('react-redux', () => ({
	useDispatch: () => mocks.dispatch,
}))

vi.mock('@/ducks/finnPerson', () => ({
	resetNavigering: () => ({ type: 'RESET_NAVIGERING' }),
}))

const originalScrollIntoView = Object.getOwnPropertyDescriptor(
	HTMLElement.prototype,
	'scrollIntoView',
)

describe('TableRow', () => {
	beforeAll(() => {
		Object.defineProperty(HTMLElement.prototype, 'scrollIntoView', {
			configurable: true,
			value: mocks.scrollIntoView,
		})
	})

	beforeEach(() => {
		vi.clearAllMocks()
	})

	afterAll(() => {
		if (originalScrollIntoView) {
			Object.defineProperty(HTMLElement.prototype, 'scrollIntoView', originalScrollIntoView)
		} else {
			delete HTMLElement.prototype.scrollIntoView
		}
	})

	it.each([
		['person', { expandPerson: true }],
		['bestilling', { expandBestilling: true }],
	])('scrolls to an automatically expanded %s row', async (_, expandProps) => {
		render(
			<TableRow expandComponent={<div>Detaljer</div>} {...expandProps}>
				Rad
			</TableRow>,
		)

		await waitFor(() => expect(screen.getByText('Detaljer')).toBeInTheDocument())

		expect(mocks.scrollIntoView).toHaveBeenCalledOnce()
		expect(mocks.scrollIntoView).toHaveBeenCalledWith({
			behavior: 'smooth',
			block: 'start',
		})
	})

	it('does not scroll during ordinary rendering', () => {
		render(
			<TableRow expandComponent={<div>Detaljer</div>}>
				Rad
			</TableRow>,
		)

		expect(mocks.scrollIntoView).not.toHaveBeenCalled()
	})
})

import { use } from 'react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router'
import { Bestillingsveileder } from '@/components/bestillingsveileder/Bestillingsveileder'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

vi.mock('react-redux', () => ({ useDispatch: () => vi.fn() }))
vi.mock('@/utils/hooks/useEnvironments', () => ({
	useDollyEnvironments: () => ({ dollyEnvironments: [] }),
}))
vi.mock('@/components/bestillingsveileder/stegVelger/StegVelger', () => ({
	StegVelger: () => {
		const context = use(BestillingsveilederContext)
		return (
			<button onClick={() => context.updateContext?.({ opprettFraIdenter: ['12345678901'] })}>
				Bytt til eksisterende identer
			</button>
		)
	},
}))

describe('Bestillingsveileder', () => {
	const originalUrl = window.location.href
	const originalState = window.history.state

	afterEach(() => {
		window.history.replaceState(originalState, '', originalUrl)
	})

	it('holder routerstaten i nettleserhistorikken oppdatert når konteksten endres', async () => {
		const user = userEvent.setup()
		render(
			<MemoryRouter
				initialEntries={[
					{ pathname: '/gruppe/5/bestilling', state: { antall: '1', identtype: 'FNR' } },
				]}
			>
				<Routes>
					<Route path="/gruppe/:gruppeId/bestilling" element={<Bestillingsveileder />} />
				</Routes>
			</MemoryRouter>,
		)

		await user.click(screen.getByRole('button', { name: 'Bytt til eksisterende identer' }))

		await waitFor(() =>
			expect(window.history.state.usr).toEqual({
				antall: '1',
				identtype: 'FNR',
				gruppeId: 5,
				opprettFraIdenter: ['12345678901'],
			}),
		)
	})
})

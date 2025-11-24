import { render, screen, waitFor } from '@testing-library/react'
import { BestillingProgresjon } from '@/components/bestilling/statusListe/BestillingProgresjon/BestillingProgresjon'
import React, { act } from 'react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { dollyTest } from '../vitest.setup'
import userEvent from '@testing-library/user-event'
import { uferdigBestillingMock } from '#/mocks/BasicMocks'
import { worker } from './mocks/browser'
import { http, HttpResponse } from 'msw'

dollyTest('renders Bestillingprogresjon and cancel removes the element', async () => {
	const user = userEvent.setup()
	const bestillinger = [uferdigBestillingMock]

	const { rerender } = render(
		<BestillingProgresjon
			bestillingID={bestillinger?.[0]?.id}
			cancelBestilling={() => bestillinger.pop()}
			onFinishBestilling={() => null}
		/>,
	)

	await waitFor(() => expect(screen.getByText('Bestillingsstatus')).toBeDefined(), {
		timeout: 2000,
	})

	worker.use(
		// override the initial uferdigBestillling request handler to return empty
		http.get('/dolly-backend/api/v1/bestilling/2', () => {
			return new HttpResponse(null, { status: 404 })
		}),
	)

	const avbrytButton = screen.getByTestId(TestComponentSelectors.BUTTON_AVBRYT_BESTILLING)
	await user.click(avbrytButton)

	act(() => {
		rerender(
			<BestillingProgresjon
				onFinishBestilling={() => null}
				bestillingID={null}
				cancelBestilling={() => bestillinger.pop()}
			/>,
		)
	})

	expect(screen.queryByText('Bestillingsstatus')).toBeNull()
})

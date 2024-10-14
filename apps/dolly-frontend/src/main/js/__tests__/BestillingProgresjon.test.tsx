import { afterAll, afterEach, beforeAll, describe, expect, test } from 'vitest'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { BestillingProgresjon } from '@/components/bestilling/statusListe/BestillingProgresjon/BestillingProgresjon'
import React from 'react'
import { http, HttpResponse } from 'msw'
import { setupServer } from 'msw/node'
import { uferdigBestillingMock } from '#/mocks/BasicMocks'
import { TestComponentSelectors } from '#/mocks/Selectors'

describe('BestillingProgresjon', () => {
	const server = setupServer(
		http.get('/dolly-backend/api/v1/bestilling/1', () => {
			return HttpResponse.json(bestillinger?.[0])
		}),
	)

	const bestillinger = [uferdigBestillingMock]

	beforeAll(() => server.listen())
	afterEach(() => server.resetHandlers())
	afterAll(() => server.close())

	test('renders', async () => {
		render(
			<BestillingProgresjon
				bestillingID={'1'}
				cancelBestilling={() => bestillinger.pop()}
				erOrganisasjon={false}
				onFinishBestilling={() => null}
			/>,
		)

		await waitFor(() => expect(screen.queryByText('Bestillingsstatus')).toBeDefined())

		const avbrytButton = screen.getByTestId(TestComponentSelectors.BUTTON_AVBRYT_BESTILLING)
		fireEvent.click(avbrytButton)

		await waitFor(() => expect(screen.queryByText('Bestillingsstatus')).toBeNull())
	})
})

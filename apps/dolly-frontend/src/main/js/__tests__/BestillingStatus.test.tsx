import { render, screen } from '@testing-library/react'
import { dollyTest } from '../vitest.setup'
import React from 'react'
import { vi } from 'vitest'
import { BestillingStatus } from '@/components/bestilling/statusListe/BestillingProgresjon/BestillingStatus'

vi.mock('@/components/ui/loading/Spinner', () => ({
	default: () => <div data-testid="spinner" />,
}))

vi.mock('@/components/ui/icon/Icon', () => ({
	default: ({ kind }: { kind: string }) => <div data-testid={`icon-${kind}`} />,
}))

vi.mock('@/components/ui/apiFeilmelding/ApiFeilmelding', () => ({
	default: ({ feilmelding }: { feilmelding: string }) => (
		<div data-testid="api-feilmelding">{feilmelding}</div>
	),
}))

dollyTest('shows info status as loading until it becomes OK', () => {
	const { rerender } = render(
		<BestillingStatus
			bestilling={{
				ferdig: false,
				antallIdenter: 1,
				feil: null,
				status: [
					{
						id: 'PDLIMPORT',
						navn: 'Pdl import',
						statuser: [{ melding: 'INFO-Tidsavbrudd' }],
					},
				],
			}}
		/>,
	)

	expect(screen.getByTestId('spinner')).toBeInTheDocument()
	expect(screen.queryByTestId('api-feilmelding')).not.toBeInTheDocument()
	expect(screen.getByText('Pdl import')).toBeInTheDocument()

	rerender(
		<BestillingStatus
			bestilling={{
				ferdig: true,
				antallIdenter: 1,
				feil: null,
				status: [
					{
						id: 'PDLIMPORT',
						navn: 'Pdl import',
						statuser: [{ melding: 'OK', identer: ['12345678901'] }],
					},
				],
			}}
		/>,
	)

	expect(screen.queryByTestId('spinner')).not.toBeInTheDocument()
	expect(screen.getByTestId('icon-feedback-check-circle')).toBeInTheDocument()
	expect(screen.queryByTestId('api-feilmelding')).not.toBeInTheDocument()
})

import { render, screen } from '@testing-library/react'
import { dollyTest } from '../vitest.setup'
import React from 'react'
import { vi } from 'vitest'
import BestillingResultat from '@/components/bestilling/statusListe/BestillingResultat/BestillingResultat'

vi.mock('@/components/feedback', () => ({ Feedback: () => <div data-testid="feedback" /> }))
vi.mock('react-confetti-explosion', () => ({
	__esModule: true,
	default: () => <div data-testid="confetti" />,
}))

dollyTest('does not show confetti for unsuccessful bestillinger', async () => {
	const lukkBestillingMock = vi.fn()

	const bestillingWithError = {
		id: '123',
		ferdig: true,
		stoppet: false,
		status: [{ statuser: [{ melding: 'Feil:random feil' }] }],
		bruker: { brukerId: 'test123' },
	}

	const { rerender } = render(
		<BestillingResultat
			bestilling={bestillingWithError}
			lukkBestilling={lukkBestillingMock}
			erOrganisasjon={false}
		/>,
	)

	expect(screen.queryByTestId('confetti')).not.toBeInTheDocument()

	const stoppedBestilling = {
		id: '123',
		ferdig: true,
		stoppet: true,
		status: [{ statuser: [] }],
		bruker: { brukerId: 'test123' },
	}

	rerender(
		<BestillingResultat
			bestilling={stoppedBestilling}
			lukkBestilling={lukkBestillingMock}
			erOrganisasjon={false}
		/>,
	)

	expect(screen.queryByTestId('confetti')).not.toBeInTheDocument()

	const incompleteBestilling = {
		id: '123',
		ferdig: false,
		stoppet: false,
		status: [{ statuser: [] }],
		bruker: { brukerId: 'test123' },
	}

	rerender(
		<BestillingResultat
			bestilling={incompleteBestilling}
			lukkBestilling={lukkBestillingMock}
			erOrganisasjon={false}
		/>,
	)

	expect(screen.queryByTestId('confetti')).not.toBeInTheDocument()
})

import { render, screen } from '@testing-library/react'
import { dollyTest } from '../vitest.setup'
import { userEvent } from 'vitest/browser'
import React, { act } from 'react'
import { vi } from 'vitest'
import { TestComponentSelectors } from '#/mocks/Selectors'
import BestillingResultat from '@/components/bestilling/statusListe/BestillingResultat/BestillingResultat'

vi.useFakeTimers()

dollyTest('renders BestillingResultat and handles confetti appearance/disappearance', async () => {
	const lukkBestillingMock = vi.fn()

	const successfulBestilling = {
		id: '123',
		ferdig: true,
		stoppet: false,
		antallIdenter: 1,
		antallLevert: 1,
		status: [{ statuser: [{ melding: 'OK' }] }],
		bruker: { brukerId: 'test123' },
	}

	render(
		<BestillingResultat
			bestilling={successfulBestilling}
			lukkBestilling={lukkBestillingMock}
			erOrganisasjon={false}
		/>,
	)

	const confettiComponent = screen.getByTestId('confetti')

	expect(screen.getByText('Bestilling #123')).toBeInTheDocument()
	expect(confettiComponent).toBeInTheDocument()

	const closeButton = screen.getByTestId(TestComponentSelectors.BUTTON_LUKK_BESTILLING_RESULTAT)
	await act(async () => {
		await userEvent.click(closeButton)
	})
	expect(lukkBestillingMock).toHaveBeenCalledWith('123')
})

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

	act(() => {
		rerender(
			<BestillingResultat
				bestilling={stoppedBestilling}
				lukkBestilling={lukkBestillingMock}
				erOrganisasjon={false}
			/>,
		)
	})

	expect(screen.queryByTestId('confetti')).not.toBeInTheDocument()

	const incompleteBestilling = {
		id: '123',
		ferdig: false,
		stoppet: false,
		status: [{ statuser: [] }],
		bruker: { brukerId: 'test123' },
	}

	act(() => {
		rerender(
			<BestillingResultat
				bestilling={incompleteBestilling}
				lukkBestilling={lukkBestillingMock}
				erOrganisasjon={false}
			/>,
		)
	})

	expect(screen.queryByTestId('confetti')).not.toBeInTheDocument()
})

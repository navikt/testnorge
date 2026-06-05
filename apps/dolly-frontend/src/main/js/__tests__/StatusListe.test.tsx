import React, { useEffect } from 'react'
import { render, screen, waitFor } from '@testing-library/react'
import { dollyTest } from '../vitest.setup'
import { vi } from 'vitest'
import StatusListe from '@/components/bestilling/statusListe/StatusListe'
import type { Bestillingsstatus } from '@/utils/hooks/useDollyOrganisasjoner'

vi.mock('@/components/bestilling/statusListe/BestillingProgresjon/BestillingProgresjon', () => ({
	BestillingProgresjon: ({
		bestillingID,
		onFinishBestilling,
	}: {
		bestillingID: string | number
		onFinishBestilling?: (bestilling: { id: string | number; ferdig: boolean }) => void
	}) => {
		useEffect(() => {
			onFinishBestilling?.({ id: bestillingID, ferdig: true })
		}, [bestillingID, onFinishBestilling])

		return <div data-testid={`progress-${bestillingID}`} />
	},
}))

vi.mock('@/components/bestilling/statusListe/BestillingResultat/BestillingResultat', () => ({
	default: ({ bestilling }: { bestilling: { id: string | number } }) => (
		<div data-testid={`result-${bestilling.id}`} />
	),
}))

vi.mock('@/components/ui/contentContainer/ContentContainer', () => ({
	default: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
}))

vi.mock('@/components/ui/loading/Loading', () => ({
	default: () => <div data-testid="loading" />,
}))

dollyTest('does not render a completed bestilling twice during transition', async () => {
	const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => undefined)

	render(
		<StatusListe
			gruppeId="1"
			brukerId="1"
			isCanceling={false}
			cancelBestilling={() => null}
			bestillingListe={[
				{
					id: 15137,
					ferdig: false,
					environments: [],
					bestilling: {},
					bruker: null,
					gruppeId: 1,
					feil: null,
					sistOppdatert: new Date(),
					opprettetFraGruppeId: 0,
					gjenopprettetFraIdent: 0,
					opprettetFraId: 0,
					status: [],
					systeminfo: '',
					stoppet: false,
				} as Bestillingsstatus,
			]}
		/>,
	)

	await waitFor(() => expect(screen.getByTestId('result-15137')).toBeInTheDocument())

	expect(screen.queryByTestId('progress-15137')).not.toBeInTheDocument()
	expect(
		consoleErrorSpy.mock.calls.some((call) =>
			String(call[0]).includes('Encountered two children with the same key'),
		),
	).toBe(false)

	consoleErrorSpy.mockRestore()
})

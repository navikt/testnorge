import React, { useContext, useEffect, useState } from 'react'
import { describe, expect, it } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { StegVelger } from '@/components/bestillingsveileder/stegVelger/StegVelger'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { ShowErrorContext } from '@/components/bestillingsveileder/ShowErrorContext'
import { MemoryRouter } from 'react-router'

const malMock = {
	id: '123',
	bestilling: {
		pdldata: {
			opprettNyPerson: { identtype: 'DNR', id2032: true },
			person: { sivilstand: [{ type: 'UGIFT' }] },
		},
	},
	malNavn: 'TestMal',
}

const InitialWrapper = ({ children }: { children: React.ReactNode }) => {
	const [state, setState] = useState<any>({
		initialValues: { pdldata: { opprettNyPerson: { identtype: 'FNR', id2032: false } } },
		identtype: 'FNR',
		id2032: false,
		mal: undefined,
		is: { nyBestilling: true },
		gruppeId: 1,
	})
	const value: BestillingsveilederContextType = {
		...state,
		setIdenttype: (identtype: string) => setState((prev: any) => ({ ...prev, identtype })),
		setGruppeId: () => {},
		setMal: (mal: any | undefined) => setState((prev: any) => ({ ...prev, mal })),
		updateContext: (patch) => setState((prev: any) => ({ ...prev, ...patch })),
	}
	return (
		<BestillingsveilederContext.Provider value={value}>
			<ShowErrorContext.Provider value={{ showError: false, setShowError: () => {} }}>
				{children}
			</ShowErrorContext.Provider>
		</BestillingsveilederContext.Provider>
	)
}

const SetMalTrigger = ({ mal }: { mal: any }) => {
	const ctx = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	useEffect(() => {
		ctx.setMal && ctx.setMal(mal)
	}, [mal])
	return null
}

describe('Mal selection sync', () => {
	it('should sync mal id, identtype and id2032 into form when setMal called', async () => {
		render(
			<MemoryRouter>
				<InitialWrapper>
					<SetMalTrigger mal={malMock} />
					<StegVelger
						initialValues={{ pdldata: { opprettNyPerson: { identtype: 'FNR', id2032: false } } }}
						onSubmit={async () => {}}
					/>
				</InitialWrapper>
			</MemoryRouter>,
		)
		await waitFor(() => {
			const snapshot = screen.getByTestId('stegevelger-form-snapshot')
			expect(snapshot.textContent).toContain('mal:123')
			expect(snapshot.textContent).toContain('identtype:DNR')
			expect(snapshot.textContent).toContain('id2032:true')
		})
		// Verify nested mal value inserted
		const sivilstand = (screen.getByTestId('stegevelger-form-snapshot').textContent || '').includes(
			'UGIFT',
		)
		expect(sivilstand).toBe(true)
	})
})

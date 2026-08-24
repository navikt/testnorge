import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { StegVelger } from '@/components/bestillingsveileder/stegVelger/StegVelger'
import { ShowErrorContext } from '@/components/bestillingsveileder/ShowErrorContext'
import { MemoryRouter } from 'react-router'

vi.mock('@/utils/hooks/useGruppe', async (importOriginal) => ({
	...(await importOriginal<typeof import('@/utils/hooks/useGruppe')>()),
	useGruppeIdenter: () => ({ identer: [] }),
	useGruppe: () => ({ gruppe: { id: 1, navn: 'Testgruppe' } }),
}))

describe('Velg egenskaper', () => {
	it.each(['Alder', 'Navn'])('should select %s in the complete order wizard', async (label) => {
		const user = userEvent.setup()
		const initialValues = {
			gruppeId: 1,
			pdldata: { opprettNyPerson: { identtype: 'FNR', id2032: false } },
		}
		const context = {
			initialValues,
			identtype: 'FNR',
			is: { nyBestilling: true },
			gruppeId: 1,
			setGruppeId: () => {},
			setIdenttype: () => {},
			setMal: () => {},
			updateContext: () => {},
		} as BestillingsveilederContextType
		sessionStorage.setItem(
			'dolly-bestilling-saved-form',
			JSON.stringify({ formValues: initialValues, step: 1, savedAt: Date.now() }),
		)

		render(
			<MemoryRouter>
				<BestillingsveilederContext.Provider value={context}>
					<ShowErrorContext.Provider value={{ showError: false, setShowError: () => {} }}>
						<StegVelger initialValues={initialValues} onSubmit={async () => {}} />
					</ShowErrorContext.Provider>
				</BestillingsveilederContext.Provider>
			</MemoryRouter>,
		)

		const checkbox = await screen.findByRole('checkbox', { name: label })
		expect(checkbox).not.toBeChecked()

		await user.click(checkbox)

		expect(checkbox).toBeChecked()
	})
})

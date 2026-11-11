import React, { useEffect } from 'react'
import { describe, expect, it, vi } from 'vitest'
import { act, render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router'
import { useFormContext } from 'react-hook-form'
import * as Yup from 'yup'
import { StegVelger } from '@/components/bestillingsveileder/stegVelger/StegVelger'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { ShowErrorContext } from '@/components/bestillingsveileder/ShowErrorContext'
import { CSRF_FAILED_EVENT } from '@/api/csrf'
import {
	clearBestillingFormState,
	loadBestillingFormState,
	saveBestillingFormState,
	savePendingBestillingFormState,
} from '@/utils/bestillingFormPersistence'
import { TestComponentSelectors } from '#/mocks/Selectors'

vi.mock('@/components/bestillingsveileder/stegVelger/steg/steg2/DollyIdentValidation', () => ({
	DollyIdentValidation: Yup.object(),
	DollyOrganisasjonValidation: Yup.object(),
}))

vi.mock('@/components/bestillingsveileder/stegVelger/steg/steg3/Steg3', () => ({
	default: function Steg3() {
		const { setValue } = useFormContext()
		useEffect(() => setValue('kommentar', 'endret'), [])
		return null
	},
}))

const context = {
	initialValues: {},
	is: { nyBestilling: true },
	gruppeId: 1,
	setIdenttype: () => {},
	setGruppeId: () => {},
	setMal: () => {},
	updateContext: () => {},
} as unknown as BestillingsveilederContextType

describe('StegVelger ved CSRF-feil', () => {
	it('husker verdiene som ble sendt inn, ikke skjemaet etter reset', async () => {
		saveBestillingFormState({ kommentar: 'lagret' }, 3)
		const onSubmit = vi.fn(async () => {})
		render(
			<MemoryRouter>
				<BestillingsveilederContext.Provider value={context}>
					<ShowErrorContext.Provider value={{ showError: false, setShowError: () => {} }}>
						<StegVelger initialValues={{}} onSubmit={onSubmit} />
					</ShowErrorContext.Provider>
				</BestillingsveilederContext.Provider>
			</MemoryRouter>,
		)

		const fullfoer = await screen.findByTestId(TestComponentSelectors.BUTTON_FULLFOER_BESTILLING)
		await act(async () => fullfoer.click())
		await waitFor(() => expect(onSubmit).toHaveBeenCalled())
		await act(async () => {})

		act(() => {
			window.dispatchEvent(new CustomEvent(CSRF_FAILED_EVENT))
		})
		savePendingBestillingFormState()

		expect(loadBestillingFormState()?.formValues?.kommentar).toBe('endret')
		clearBestillingFormState()
	})
})

import React, { useState } from 'react'
import { describe, expect, it } from 'vitest'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { FormProvider, useForm } from 'react-hook-form'
import { VelgIdenttype } from '@/components/bestillingsveileder/stegVelger/steg/steg0/VelgIdenttype'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { deriveBestillingsveilederState } from '@/components/bestillingsveileder/options/deriveBestillingsveilederState'

const makeNyContext = (): BestillingsveilederContextType => {
	const derived = deriveBestillingsveilederState({ antall: 1, identtype: 'FNR' }, [])
	return {
		...derived,
		initialValues: derived.initialValues,
		setIdenttype: () => {},
		setGruppeId: () => {},
		setMal: () => {},
		updateContext: () => {},
	}
}

const TestWrapper = () => {
	const [ctx, setCtx] = useState<BestillingsveilederContextType>(() => makeNyContext())
	const formMethods = useForm({ defaultValues: ctx.initialValues, mode: 'onChange' })
	const updateContext = (patch: Partial<BestillingsveilederContextType>) => {
		setCtx((prev) => ({ ...prev, ...patch, is: { ...prev.is, ...(patch as any).is } }))
	}
	const contextValue: BestillingsveilederContextType = { ...ctx, updateContext }
	return (
		<BestillingsveilederContext.Provider value={contextValue}>
			<FormProvider {...formMethods}>
				<VelgIdenttype gruppeId={null} />
				<div data-testid="form-values">{JSON.stringify(formMethods.getValues())}</div>
				<div data-testid="context-flags">{JSON.stringify(contextValue.is)}</div>
			</FormProvider>
		</BestillingsveilederContext.Provider>
	)
}

describe('VelgIdenttype toggling', () => {
	it('should start with ny initial values and switch to opprettFraIdenter values', async () => {
		render(<TestWrapper />)
		const nyRadio = screen.getByRole('radio', { name: /Ny person/i })
		expect(nyRadio).toHaveAttribute('aria-checked', 'true')
		let formSnapshot = screen.getByTestId('form-values').textContent || ''
		expect(formSnapshot).toContain('"pdldata"')
		expect(formSnapshot).toContain('"identtype":"FNR"')
		const eksisterendeRadio = screen.getByRole('radio', { name: /Eksisterende person/i })
		fireEvent.click(eksisterendeRadio)
		await waitFor(() => {
			formSnapshot = screen.getByTestId('form-values').textContent || ''
			expect(formSnapshot).not.toContain('"pdldata"')
			expect(formSnapshot).toContain('"opprettFraIdenter":[]')
			const flags = screen.getByTestId('context-flags').textContent || ''
			expect(flags).toContain('"opprettFraIdenter":true')
		})
	})

	it('should switch back to ny and restore pdldata structure', async () => {
		render(<TestWrapper />)
		const eksisterendeRadio = screen.getByRole('radio', { name: /Eksisterende person/i })
		fireEvent.click(eksisterendeRadio)
		await waitFor(() => {
			const flags = screen.getByTestId('context-flags').textContent || ''
			expect(flags).toContain('"opprettFraIdenter":true')
		})
		const nyRadio = screen.getByRole('radio', { name: /Ny person/i })
		fireEvent.click(nyRadio)
		await waitFor(() => {
			const formSnapshot = screen.getByTestId('form-values').textContent || ''
			expect(formSnapshot).toContain('"pdldata"')
			expect(formSnapshot).toContain('"identtype":"FNR"')
			const flags = screen.getByTestId('context-flags').textContent || ''
			expect(flags).toContain('"opprettFraIdenter":false')
		})
	})
})

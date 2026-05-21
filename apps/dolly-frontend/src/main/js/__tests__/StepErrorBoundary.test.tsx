import { afterEach, describe, expect, it, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import StepErrorBoundary from '@/components/bestillingsveileder/stegVelger/StepErrorBoundary'
import React from 'react'

vi.mock('@/logger/Logger', () => ({
	Logger: {
		error: vi.fn(),
		trace: vi.fn(),
		log: vi.fn(),
		warn: vi.fn(),
	},
}))

const ThrowingChild = ({ error }: { error: Error }) => {
	throw error
}

const GoodChild = () => <div>Step content rendered</div>

describe('StepErrorBoundary', () => {
	const originalConsoleError = console.error

	afterEach(() => {
		console.error = originalConsoleError
		vi.restoreAllMocks()
	})

	it('should render children when no error occurs', () => {
		render(
			<StepErrorBoundary stepIndex={0} stepLabel="Velg gruppe">
				<GoodChild />
			</StepErrorBoundary>,
		)

		expect(screen.getByText('Step content rendered')).toBeDefined()
	})

	it('should render error alert when child throws', () => {
		console.error = vi.fn()

		render(
			<StepErrorBoundary stepIndex={1} stepLabel="Velg egenskaper">
				<ThrowingChild error={new Error('Something broke')} />
			</StepErrorBoundary>,
		)

		expect(screen.getByText(/Noe gikk galt ved visning av "Velg egenskaper"/)).toBeDefined()
	})

	it('should render chunk error message for chunk load errors', () => {
		console.error = vi.fn()

		render(
			<StepErrorBoundary stepIndex={2} stepLabel="Velg verdier">
				<ThrowingChild
					error={new Error('Failed to fetch dynamically imported module: /assets/Steg2-abc.js')}
				/>
			</StepErrorBoundary>,
		)

		expect(screen.getByText(/En ny versjon av Dolly er tilgjengelig/)).toBeDefined()
	})

	it('should render reload button on error', () => {
		console.error = vi.fn()

		render(
			<StepErrorBoundary stepIndex={1} stepLabel="Velg egenskaper">
				<ThrowingChild error={new Error('Render failed')} />
			</StepErrorBoundary>,
		)

		expect(screen.getByText('Last inn siden på nytt')).toBeDefined()
	})

	it('should not render null on error (old behavior)', () => {
		console.error = vi.fn()

		const { container } = render(
			<StepErrorBoundary stepIndex={0} stepLabel="Velg gruppe">
				<ThrowingChild error={new Error('Crash')} />
			</StepErrorBoundary>,
		)

		expect(container.innerHTML).not.toBe('')
	})
})

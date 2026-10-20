import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'

import { FagsystemStatusCard } from '@/pages/StatusPage/FagsystemStatusCard'
import { FagsystemStatus } from '@/services/statusApi'

const startedAt = '2026-09-21T10:00:00Z'

const status: FagsystemStatus = {
	systemId: 'arena',
	displayName: 'Arena',
	environment: 'Q1',
	runId: 'aaf62d6f-eb87-49ce-bcef-b82ca3fd940d',
	state: 'CLEANUP',
	startedAt,
	completedAt: null,
	cachedUntil: null,
	cleanupAttempts: 1,
	error: null,
	technicalStatus: {
		state: 'UNKNOWN',
		checkedAt: null,
	},
}

afterEach(cleanup)

describe('FagsystemStatusCard', () => {
	it('should show an individual loader while cleanup runs', () => {
		render(
			<FagsystemStatusCard
				statuses={[status]}
				anyRunActive={true}
				starting={false}
				now={new Date(startedAt).getTime()}
				onRerun={vi.fn()}
			/>
		)

		expect(screen.getByTitle('Tester Arena')).toBeInTheDocument()
		expect(screen.getByText('Kjører')).toBeInTheDocument()
	})

	it('should keep rerun focusable during cooldown without starting a test', () => {
		const rerun = vi.fn()
		render(
			<FagsystemStatusCard
				statuses={[{ ...status, state: 'OK', completedAt: startedAt }]}
				anyRunActive={false}
				starting={false}
				now={new Date(startedAt).getTime() + 60_000}
				onRerun={rerun}
			/>
		)

		const button = screen.getByRole('button', { name: 'Kjør på nytt' })
		expect(button).toHaveAttribute('aria-disabled', 'true')
		expect(screen.getByText(/Kan kjøres på nytt/)).toBeVisible()
		fireEvent.click(button)
		expect(rerun).not.toHaveBeenCalled()
	})

	it('should start a new test after cooldown', () => {
		const rerun = vi.fn()
		render(
			<FagsystemStatusCard
				statuses={[{ ...status, state: 'OK', completedAt: startedAt }]}
				anyRunActive={false}
				starting={false}
				now={new Date(startedAt).getTime() + 6 * 60_000}
				onRerun={rerun}
			/>
		)

		fireEvent.click(screen.getByRole('button', { name: 'Kjør på nytt' }))
		expect(rerun).toHaveBeenCalledWith('arena')
	})
})

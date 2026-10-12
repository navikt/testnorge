import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import {
	DashboardChartPanel,
	DashboardKpiCard,
	DashboardSelectButtons,
} from '@/pages/adminPages/Dashboard/dashboardSharedComponents'
import { createPersonTrendChartOptions } from '@/pages/adminPages/Dashboard/dashboardTrendChartOptions'
import type { PersonTrendPoint } from '@/pages/adminPages/Dashboard/dashboardUtils'

const makePoints = (count: number, base: number): PersonTrendPoint[] =>
	Array.from({ length: count }, (_, index) => ({
		dato: `2026-01-${String(index + 1).padStart(2, '0')}`,
		datoVisning: `${index + 1}. jan`,
		personerTotalt: base * 10 + index,
		nye: base + index,
		gjenopprettede: base + index + 1,
	}))

const visibilityOptions = {
	personerTotaltVisible: false,
	onPersonerTotaltVisibilityChange: () => undefined,
}

const optionsFor = (count: number, base: number) =>
	createPersonTrendChartOptions(makePoints(count, base), visibilityOptions)

describe('DashboardChartPanel', () => {
	it('should re-render across changing datasets without throwing (Highcharts 13 setData regression)', () => {
		const { rerender } = render(
			<DashboardChartPanel options={optionsFor(7, 0)} ariaLabel="Persontrend" />,
		)

		const transitions = [optionsFor(7, 9), optionsFor(20, 5), optionsFor(5, 3), optionsFor(31, 2)]
		transitions.forEach((nextOptions) => {
			expect(() =>
				rerender(<DashboardChartPanel options={nextOptions} ariaLabel="Persontrend" />),
			).not.toThrow()
		})
	})

	it('should not throw when re-rendered with structurally unchanged options', () => {
		const points = makePoints(12, 5)
		const { rerender } = render(
			<DashboardChartPanel
				options={createPersonTrendChartOptions(points, visibilityOptions)}
				ariaLabel="Persontrend"
			/>,
		)

		expect(() =>
			rerender(
				<DashboardChartPanel
					options={createPersonTrendChartOptions(points, visibilityOptions)}
					ariaLabel="Persontrend"
				/>,
			),
		).not.toThrow()
	})
})

describe('DashboardSelectButtons', () => {
	it('should expose the selected option with aria-pressed', () => {
		render(
			<DashboardSelectButtons
				label="År"
				selected="2026"
				onSelect={() => undefined}
				options={[
					{ value: '2025', label: '2025' },
					{ value: '2026', label: '2026' },
				]}
			/>,
		)

		expect(screen.getByRole('button', { name: '2026', pressed: true })).toBeInTheDocument()
		expect(screen.getByRole('button', { name: '2025', pressed: false })).toBeInTheDocument()
	})

	it('should support compact buttons for long option lists', () => {
		render(
			<DashboardSelectButtons
				selected="2026-01"
				onSelect={() => undefined}
				options={[{ value: '2026-01', label: 'Januar' }]}
				size="xsmall"
				gap="space-4"
			/>,
		)

		expect(screen.getByRole('button', { name: 'Januar' })).toHaveClass('aksel-button--xsmall')
	})
})

describe('DashboardKpiCard', () => {
	it('should show the value as text, not as a heading', () => {
		render(<DashboardKpiCard label="Nye personer" value={17} />)

		expect(screen.getByText('Nye personer')).toBeInTheDocument()
		expect(screen.getByText('17').tagName).toBe('P')
		expect(screen.queryByRole('heading')).toBeNull()
	})
})

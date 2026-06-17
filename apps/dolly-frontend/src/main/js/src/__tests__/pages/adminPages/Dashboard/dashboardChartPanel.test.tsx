import { render } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { DashboardChartPanel } from '@/pages/adminPages/Dashboard/dashboardSharedComponents'
import { createPersonTrendChartOptions } from '@/pages/adminPages/Dashboard/dashboardTrendChartOptions'
import type { PersonTrendPoint } from '@/pages/adminPages/Dashboard/dashboardUtils'

const makePoints = (count: number, base: number): PersonTrendPoint[] =>
	Array.from({ length: count }, (_, index) => ({
		dato: `2026-01-${String(index + 1).padStart(2, '0')}`,
		datoVisning: `${index + 1}. jan`,
		personerTotalt: base * 10 + index,
		nye: base + index,
		gjenopprettede: base + index + 1,
		pdlFeil: index,
		andreFeil: index + 1,
	}))

const visibilityOptions = {
	personerTotaltVisible: false,
	feilTotaltVisible: false,
	onPersonerTotaltVisibilityChange: () => undefined,
	onFeilTotaltVisibilityChange: () => undefined,
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

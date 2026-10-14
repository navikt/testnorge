import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'
import { PeriodeVelger } from '@/pages/statistikk/PeriodeVelger'

const props = {
	aarOptions: ['2025', '2026'],
	valgtAar: '2026',
	onAarChange: vi.fn(),
	maanedOptions: [
		{ value: '2026-02', label: 'feb.' },
		{ value: '2026-09', label: 'sep.' },
	],
	valgtMaaned: '2026-09',
	onMaanedChange: vi.fn(),
	visning: 'maaned' as const,
	onVisningChange: vi.fn(),
}

const trykketInn = (gruppe: string) =>
	within(screen.getByRole('group', { name: gruppe }))
		.getAllByRole('button')
		.map((knapp) => knapp.getAttribute('aria-pressed'))

describe('PeriodeVelger', () => {
	beforeEach(() => {
		vi.useFakeTimers({ toFake: ['Date'] })
		vi.setSystemTime(new Date(2026, 9, 13))
	})

	afterEach(() => {
		vi.useRealTimers()
	})

	it('should mark only the selected month in the month view', () => {
		render(<PeriodeVelger {...props} />)

		expect(trykketInn('År')).toEqual(['false', 'true'])
		expect(trykketInn('Måned')).toEqual(['false', 'true'])
		expect(screen.queryByRole('button', { name: /med i tidsrommet/ })).toBeNull()

		const visning = screen.getByRole('radiogroup', { name: 'Vis statistikk for' })
		expect(
			within(visning)
				.getAllByRole('radio')
				.map((radio) => radio.textContent),
		).toEqual(['Kun valgt måned', 'Hittil i 2026'])
		expect(
			within(visning).getByRole('radio', { name: 'Kun valgt måned' }).getAttribute('aria-checked'),
		).toBe('true')
	})

	it('should mark every month of the year as included in the year view', () => {
		render(<PeriodeVelger {...props} valgtAar="2025" visning="aar" />)

		expect(trykketInn('Måned')).toEqual(['false', 'false'])
		expect(screen.getByRole('button', { name: /^feb\.\s*, med i tidsrommet$/ })).toBeTruthy()
		expect(screen.getByRole('button', { name: /^sep\.\s*, med i tidsrommet$/ })).toBeTruthy()
		expect(screen.getByRole('radio', { name: 'Hele 2025' }).getAttribute('aria-checked')).toBe(
			'true',
		)
	})

	it('should report year, month and view changes', async () => {
		const onAarChange = vi.fn()
		const onMaanedChange = vi.fn()
		const onVisningChange = vi.fn()
		render(
			<PeriodeVelger
				{...props}
				onAarChange={onAarChange}
				onMaanedChange={onMaanedChange}
				onVisningChange={onVisningChange}
			/>,
		)

		await userEvent.click(screen.getByRole('button', { name: '2025' }))
		await userEvent.click(screen.getByRole('button', { name: 'feb.' }))
		await userEvent.click(screen.getByRole('radio', { name: 'Hittil i 2026' }))

		expect(onAarChange).toHaveBeenCalledWith('2025')
		expect(onMaanedChange).toHaveBeenCalledWith('2026-02')
		expect(onVisningChange).toHaveBeenCalledWith('aar')
	})
})

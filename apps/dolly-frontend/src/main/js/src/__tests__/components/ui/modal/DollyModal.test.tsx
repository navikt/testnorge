import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { DollyModal } from '@/components/ui/modal/DollyModal'

describe('DollyModal', () => {
	it('preserves the existing modal heading styles', () => {
		render(
			<DollyModal isOpen closeModal={vi.fn()}>
				<h1>Modaloverskrift</h1>
			</DollyModal>,
		)

		const heading = screen.getByRole('heading', { name: 'Modaloverskrift' })

		expect(getComputedStyle(heading).fontSize).toBe('32px')
		expect(getComputedStyle(heading).marginTop).toBe('0px')
	})
})

import { render, screen } from '@testing-library/react'
import { Bestillingsvisning } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'

vi.mock('@/components/bestilling/sammendrag/bestillingsdata/Bestillingsdata', () => ({
	Bestillingsdata: () => <div data-testid="bestillingsdata">Bestillingsdata</div>,
}))

const bestilling = (tjenestevirksomhet: string) => ({
	pdldata: {
		person: {
			vergemaal: [{ tjenesteomraade: [{ tjenestevirksomhet, tjenesteoppgave: ['pensjon'] }] }],
		},
	},
})

describe('Bestillingsvisning', () => {
	it('should show the vergemaal warning outside the collapsible summary', () => {
		render(<Bestillingsvisning bestilling={bestilling('kommune')} />)

		const advarsel = screen.getByText('Vergemålet mangler Nav som tjenestevirksomhet.')
		const sammenleggbart = screen.getByTestId('bestillingsdata').parentElement

		expect(sammenleggbart?.contains(advarsel)).toBe(false)
	})

	it('should not show the vergemaal warning when nav is present', () => {
		render(<Bestillingsvisning bestilling={bestilling('nav')} />)

		expect(screen.queryByText(/mangler Nav som tjenestevirksomhet/)).not.toBeInTheDocument()
	})
})

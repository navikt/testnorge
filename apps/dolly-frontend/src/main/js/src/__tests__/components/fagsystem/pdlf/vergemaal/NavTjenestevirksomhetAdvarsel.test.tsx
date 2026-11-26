import { render, screen } from '@testing-library/react'
import {
	harNavTjenestevirksomhet,
	NavTjenestevirksomhetAdvarsel,
	VergemaalOppsummeringAdvarsel,
} from '@/components/fagsystem/pdlf/form/partials/vergemaal/NavTjenestevirksomhetAdvarsel'
import { Vergemaal } from '@/components/fagsystem/pdlf/bestilling/Vergemaal'
import { initialVergemaal } from '@/components/fagsystem/pdlf/form/initialValues'

const advarsel = /Legg til et tjenesteområde der tjenestevirksomhet er «nav»/

describe('harNavTjenestevirksomhet', () => {
	it('should accept nav regardless of case and surrounding whitespace', () => {
		expect(
			harNavTjenestevirksomhet([{ tjenestevirksomhet: ' NAV ', tjenesteoppgave: ['pensjon'] }]),
		).toBe(true)
		expect(
			harNavTjenestevirksomhet([
				{ tjenestevirksomhet: 'annen', tjenesteoppgave: ['arbeid'] },
				{ tjenestevirksomhet: 'nav', tjenesteoppgave: ['pensjon'] },
			]),
		).toBe(true)
	})

	it('should reject missing, empty and non-nav tjenesteomraade', () => {
		expect(harNavTjenestevirksomhet(undefined)).toBe(false)
		expect(harNavTjenestevirksomhet([])).toBe(false)
		expect(harNavTjenestevirksomhet([{ tjenestevirksomhet: 'kommune' }])).toBe(false)
	})

	it('should reject nav without tjenesteoppgave since pdl-forvalter sends one entry per oppgave', () => {
		expect(harNavTjenestevirksomhet([{ tjenestevirksomhet: 'nav', tjenesteoppgave: [] }])).toBe(
			false,
		)
		expect(harNavTjenestevirksomhet([{ tjenestevirksomhet: 'nav' }])).toBe(false)
	})
})

describe('NavTjenestevirksomhetAdvarsel', () => {
	it('should show warning when no tjenesteomraade has nav', () => {
		render(<NavTjenestevirksomhetAdvarsel tjenesteomraade={[]} />)

		expect(screen.getByText(advarsel)).toBeInTheDocument()
	})

	it('should hide warning for the default tjenesteomraade', () => {
		render(<NavTjenestevirksomhetAdvarsel tjenesteomraade={initialVergemaal.tjenesteomraade} />)

		expect(screen.queryByText(advarsel)).not.toBeInTheDocument()
	})

	it('should announce warning in a live region that stays mounted', () => {
		const { rerender } = render(
			<NavTjenestevirksomhetAdvarsel tjenesteomraade={initialVergemaal.tjenesteomraade} />,
		)
		const status = screen.getByRole('status')
		expect(status).toBeEmptyDOMElement()

		rerender(<NavTjenestevirksomhetAdvarsel tjenesteomraade={[]} />)

		expect(screen.getByRole('status')).toBe(status)
		expect(status).toHaveTextContent(advarsel)
	})
})

describe('Vergemaal bestillingsvisning', () => {
	it('should show warning in summary when nav is missing', () => {
		render(
			<Vergemaal
				vergemaalListe={[
					{
						id: 1,
						tjenesteomraade: [{ tjenestevirksomhet: 'kommune', tjenesteoppgave: ['pensjon'] }],
					} as never,
				]}
			/>,
		)

		expect(screen.getByText(advarsel)).toBeInTheDocument()
	})

	it('should show warning in summary when nav has no tjenesteoppgave', () => {
		render(
			<Vergemaal
				vergemaalListe={[
					{
						id: 1,
						tjenesteomraade: [{ tjenestevirksomhet: 'nav', tjenesteoppgave: [] }],
					} as never,
				]}
			/>,
		)

		expect(screen.getByText(advarsel)).toBeInTheDocument()
	})

	it('should not show warning in summary when nav is present', () => {
		render(
			<Vergemaal
				vergemaalListe={[
					{
						id: 1,
						tjenesteomraade: [{ tjenestevirksomhet: 'nav', tjenesteoppgave: ['hjelpemidler'] }],
					} as never,
				]}
			/>,
		)

		expect(screen.queryByText(advarsel)).not.toBeInTheDocument()
	})
})

const medNav = { tjenesteomraade: [{ tjenestevirksomhet: 'nav', tjenesteoppgave: ['pensjon'] }] }
const utenNav = {
	tjenesteomraade: [{ tjenestevirksomhet: 'kommune', tjenesteoppgave: ['pensjon'] }],
}

describe('VergemaalOppsummeringAdvarsel', () => {
	it('should name which vergemaal lack nav when there are several', () => {
		render(<VergemaalOppsummeringAdvarsel vergemaalListe={[medNav, utenNav, utenNav] as never} />)

		expect(
			screen.getByText('Vergemål 2, 3 mangler Nav som tjenestevirksomhet.'),
		).toBeInTheDocument()
		expect(screen.getByText(/Gå tilbake til Vergemål under Personinformasjon/)).toBeInTheDocument()
	})

	it('should render nothing when every vergemaal has nav', () => {
		const { container } = render(
			<VergemaalOppsummeringAdvarsel vergemaalListe={[medNav] as never} />,
		)

		expect(container).toBeEmptyDOMElement()
	})
})

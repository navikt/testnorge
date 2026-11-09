import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'
import { KdiVisning } from '@/components/fagsystem/kdi/visning/KdiVisning'
import { KdiBestilling } from '@/components/fagsystem/kdi/bestilling/KdiBestilling'
import type { KdiProps } from '@/components/fagsystem/kdi/form/partials/types'

vi.mock('@/utils/hooks/useInstitusjon', () => ({
	useFengsel: () => ({ fengsler: { '874718602': 'Testfengsel' } }),
}))

const melding = (meldingId: string | null, hendelseId: string, publiseringstidspunkt: string) => ({
	meldingId,
	hendelseId,
	publiseringstidspunkt,
	organisasjonsnummer: '874718602',
})

const kdiMedMeldinger = (antall: number) =>
	({
		innsettelse: Array.from({ length: antall }, (_, idx) =>
			melding(`m${idx}`, 'h1', `2026-06-0${idx + 1}T10:00:00`),
		),
		loeslatelse: [melding('l1', 'h2', '2026-06-20T10:00:00')],
	}) as unknown as KdiProps

describe('KdiVisning', () => {
	it('should show number of meldinger in header for each hendelse', () => {
		render(<KdiVisning data={kdiMedMeldinger(2)} loading={false} harKdiBestilling />)

		expect(screen.getByText(/Innsettelse \(2 meldinger\)/)).toBeTruthy()
		expect(screen.getByText(/Løslatelse \(1 melding\)/)).toBeTruthy()
	})

	it('should show hendelser expanded when there are 5 meldinger or fewer', () => {
		render(<KdiVisning data={kdiMedMeldinger(4)} loading={false} harKdiBestilling />)

		expect(screen.getAllByText('Publiseringstidspunkt')).toHaveLength(5)
	})

	it('should show hendelser as expandable when there are more than 5 meldinger', async () => {
		render(<KdiVisning data={kdiMedMeldinger(5)} loading={false} harKdiBestilling />)

		expect(screen.queryByText('Publiseringstidspunkt')).toBeNull()

		await userEvent.click(screen.getByText(/Løslatelse \(1 melding\)/))

		expect(screen.getAllByText('Publiseringstidspunkt')).toHaveLength(1)
	})
})

describe('KdiBestilling', () => {
	const kdi = {
		innsettelse: [
			melding('m1', 'h1', '2026-06-01T10:00:00'),
			melding(null, 'h1', '2026-06-02T10:00:00'),
		],
	} as unknown as KdiProps

	it('should not show melding titles by default', () => {
		render(<KdiBestilling kdi={kdi} />)

		expect(screen.queryByText('Eksisterende')).toBeNull()
		expect(screen.queryByText('Redigering')).toBeNull()
	})

	it('should show melding titles when visMeldingTittel is set', () => {
		render(<KdiBestilling kdi={kdi} visMeldingTittel />)

		expect(screen.getByText('Eksisterende')).toBeTruthy()
		expect(screen.getByText('Redigering')).toBeTruthy()
	})
})

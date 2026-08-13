import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ArbeidssoekerregisteretVisning } from '@/components/fagsystem/arbeidssoekerregisteret/visning/ArbeidssoekerregisteretVisning'
import type { ArbeidssoekerregisteretTypes } from '@/components/fagsystem/arbeidssoekerregisteret/arbeidssoekerregisteretTypes'
import { Logger } from '@/logger/Logger'

vi.mock(
	'@/components/fagsystem/arbeidssoekerregisteret/visning/ArbeidssoekerregisteretStopp',
	() => ({
		ArbeidssoekerregisteretStopp: ({ onStopped }: { onStopped: () => void }) => (
			<button onClick={onStopped}>Stopp</button>
		),
	}),
)

vi.mock('@/logger/Logger', () => ({
	Logger: {
		error: vi.fn(),
	},
}))

vi.mock('@/utils/hooks/useArbeidssoekerregisteret', () => ({
	useArbeidssoekerTyper: () => ({
		data: [],
		loading: false,
		error: undefined,
	}),
}))

const registrering: ArbeidssoekerregisteretTypes = {
	utfoertAv: 'SLUTTBRUKER',
	kilde: 'Dolly',
	aarsak: 'UKJENT',
	nuskode: '4',
	utdanningBestaatt: true,
	utdanningGodkjent: true,
	jobbsituasjonsbeskrivelse: 'HAR_SAGT_OPP',
	jobbsituasjonsdetaljer: {
		gjelderFraDato: new Date('2026-01-01'),
		gjelderTilDato: new Date('2026-02-01'),
		stillingStyrk08: '1234',
		stillingstittel: 'Utvikler',
		stillingsprosent: '100',
		sisteDagMedLoenn: new Date('2026-02-01'),
		sisteArbeidsdag: new Date('2026-01-31'),
	},
	helsetilstandHindrerArbeid: false,
	andreForholdHindrerArbeid: false,
	registreringstidspunkt: new Date('2026-01-01'),
}

describe('ArbeidssoekerregisteretVisning', () => {
	beforeEach(() => {
		vi.clearAllMocks()
	})

	it('viser stoppet-varsel etter stopp og revaliderer data', async () => {
		const onRefresh = vi.fn().mockResolvedValue(undefined)

		render(
			<ArbeidssoekerregisteretVisning
				data={registrering}
				ident="12345678901"
				onRefresh={onRefresh}
			/>,
		)

		fireEvent.click(screen.getByRole('button', { name: 'Stopp' }))

		expect(screen.getByText('Registrering som arbeidssøker er stoppet.')).toBeInTheDocument()
		expect(screen.queryByRole('button', { name: 'Stopp' })).not.toBeInTheDocument()
		await waitFor(() => expect(onRefresh).toHaveBeenCalledTimes(1))
	})

	it('viser ikke stoppet-varsel for en annen ident', () => {
		const onRefresh = vi.fn().mockResolvedValue(undefined)
		const { rerender } = render(
			<ArbeidssoekerregisteretVisning
				key="12345678901"
				data={registrering}
				ident="12345678901"
				onRefresh={onRefresh}
			/>,
		)

		fireEvent.click(screen.getByRole('button', { name: 'Stopp' }))

		rerender(
			<ArbeidssoekerregisteretVisning
				key="10987654321"
				data={registrering}
				ident="10987654321"
				onRefresh={onRefresh}
			/>,
		)

		expect(screen.queryByText('Registrering som arbeidssøker er stoppet.')).not.toBeInTheDocument()
		expect(screen.getByRole('button', { name: 'Stopp' })).toBeInTheDocument()

		rerender(
			<ArbeidssoekerregisteretVisning
				key="12345678901"
				data={registrering}
				ident="12345678901"
				onRefresh={onRefresh}
			/>,
		)

		expect(screen.queryByText('Registrering som arbeidssøker er stoppet.')).not.toBeInTheDocument()
	})

	it('beholder stoppet-varselet når revalidering feiler', async () => {
		const onRefresh = vi.fn().mockRejectedValue(new Error('Oppfriskning feilet'))

		render(
			<ArbeidssoekerregisteretVisning
				data={registrering}
				ident="12345678901"
				onRefresh={onRefresh}
			/>,
		)

		fireEvent.click(screen.getByRole('button', { name: 'Stopp' }))

		expect(screen.getByText('Registrering som arbeidssøker er stoppet.')).toBeInTheDocument()
		await waitFor(() =>
			expect(Logger.error).toHaveBeenCalledWith({
				event: 'Oppfriskning av arbeidssøkerregisteret etter stopp feilet',
				message: 'Oppfriskning feilet',
				uuid: window.uuid,
			}),
		)
	})
})

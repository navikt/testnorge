import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { FormProvider, useForm } from 'react-hook-form'
import { vi } from 'vitest'
import type { ReactNode } from 'react'
import { KdiForm } from '@/components/fagsystem/kdi/form/Form'

vi.mock('@/components/bestillingsveileder/VisAttributt', () => ({
	Vis: ({ children }: { children: ReactNode }) => <>{children}</>,
}))
vi.mock('@/components/ui/panel/Panel', () => ({
	default: ({ children }: { children: ReactNode }) => <>{children}</>,
}))
vi.mock('@/components/ui/form/formUtils', () => ({
	erForsteEllerTest: () => true,
	usePanelError: () => false,
}))
vi.mock('@/utils/hooks/useInstitusjon', () => ({
	useFengsel: () => ({ fengsler: { '874718602': 'Testfengsel' } }),
}))

const innsettelse = (meldingId: string, hendelseId: string, publiseringstidspunkt: string) => ({
	meldingId,
	hendelseId,
	publiseringstidspunkt,
	kategori: 'Boetesoning',
	organisasjonsnummer: '874718602',
	tidspunkt: '2026-06-01T06:00:00',
})

const TestHarness = () => {
	const formMethods = useForm({
		defaultValues: {
			instdataKdi: {
				innsettelse: [
					innsettelse('m2', 'hendelse-aaaaaa', '2026-06-18T09:00:00'),
					innsettelse('m1', 'hendelse-aaaaaa', '2026-06-16T17:11:30'),
					innsettelse('m3', 'hendelse-bbbbbb', '2026-06-10T10:00:00'),
				],
				loeslatelse: [],
				avbruddStart: [],
				avbruddSlutt: [],
				forventetLoeslatelse: [],
				annullering: [
					{
						meldingId: 'a1',
						hendelseId: 'annet',
						publiseringstidspunkt: '2026-06-11T10:00:00',
						annullertMeldingId: 'm3',
					},
				],
			},
		},
	})
	return (
		<FormProvider {...formMethods}>
			<KdiForm />
		</FormProvider>
	)
}

describe('KdiForm', () => {
	it('should group meldinger by hendelse', async () => {
		render(<TestHarness />)

		const kort = await screen.findAllByRole('region')
		expect(kort).toHaveLength(2)
		expect(within(kort[0]).getByText(/…bbbbbb · 1 melding/)).toBeTruthy()
		expect(within(kort[0]).getByText('Annullert')).toBeTruthy()
		expect(within(kort[1]).getByText(/…aaaaaa · 2 meldinger/)).toBeTruthy()

		await userEvent.click(within(kort[0]).getByRole('button', { name: /Vis/ }))
		expect(within(kort[0]).queryByRole('button', { name: /Rediger melding/ })).toBeNull()

		await userEvent.click(within(kort[1]).getByRole('button', { name: /Vis/ }))
		expect(within(kort[1]).getByText('Opprettet')).toBeTruthy()
		expect(within(kort[1]).getByText('Redigert')).toBeTruthy()
		expect(within(kort[1]).getAllByRole('button', { name: /Annuller melding/ })).toHaveLength(2)

		await userEvent.click(within(kort[1]).getByRole('button', { name: /Rediger melding/ }))
		const oppdatertKort = screen.getAllByRole('region')
		expect(within(oppdatertKort[1]).getByText('Endret')).toBeTruthy()
		expect(within(oppdatertKort[1]).getByText('Ny redigering')).toBeTruthy()
		expect(
			within(oppdatertKort[1]).getAllByRole('button', { name: /Rediger melding/ }),
		).toHaveLength(1)
	})
})

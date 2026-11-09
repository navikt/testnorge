import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { FieldValues, FormProvider, useForm, UseFormReturn } from 'react-hook-form'
import { vi } from 'vitest'
import { ReactNode, useEffect } from 'react'
import { KdiForm, publiseringstidspunktTid } from '@/components/fagsystem/kdi/form/Form'

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

const melding = (meldingId: string, hendelseId: string, publiseringstidspunkt: string) => ({
	meldingId,
	hendelseId,
	publiseringstidspunkt,
	kategori: 'Boetesoning',
	organisasjonsnummer: '874718602',
	tidspunkt: '2026-06-01T06:00:00',
})

const annullering = (meldingId: string, annullertMeldingId: string) => ({
	meldingId,
	hendelseId: `annullering-${meldingId}`,
	publiseringstidspunkt: '2026-06-20T10:00:00',
	annullertMeldingId,
})

const standardKdi = {
	innsettelse: [
		melding('m2', 'hendelse-aaaaaa', '2026-06-18T09:00:00'),
		melding('m1', 'hendelse-aaaaaa', '2026-06-16T17:11:30'),
		melding('m3', 'hendelse-bbbbbb', '2026-06-10T10:00:00'),
	],
	loeslatelse: [melding('l1', 'hendelse-aaaaaa', '2026-06-12T08:00:00')],
	avbruddStart: [],
	avbruddSlutt: [],
	forventetLoeslatelse: [],
	annullering: [annullering('a1', 'm3'), annullering('a2', 'm1')],
}

type TestHarnessProps = {
	instdataKdi?: Record<string, Array<Record<string, unknown>>>
	onFormMethods?: (formMethods: UseFormReturn) => void
}

const TestHarness = ({ instdataKdi = standardKdi, onFormMethods }: TestHarnessProps) => {
	const formMethods = useForm<FieldValues>({ defaultValues: { instdataKdi } })
	useEffect(() => {
		onFormMethods?.(formMethods)
	}, [formMethods, onFormMethods])
	return (
		<FormProvider {...formMethods}>
			<KdiForm />
		</FormProvider>
	)
}

const hentKort = async () => {
	const kort = await screen.findAllByRole('region')
	const [annullertKort, loeslatelseKort, innsettelseKort] = kort
	return { kort, annullertKort, loeslatelseKort, innsettelseKort }
}

const aapneKort = (kort: HTMLElement) =>
	userEvent.click(within(kort).getByRole('button', { name: /Vis/ }))

describe('KdiForm', () => {
	it('should group meldinger by type and hendelse and sort hendelser by latest melding', async () => {
		render(<TestHarness />)

		const { kort, annullertKort, loeslatelseKort, innsettelseKort } = await hentKort()
		expect(kort).toHaveLength(3)
		expect(within(annullertKort).getByRole('heading', { level: 3 }).textContent).toBe('Innsettelse')
		expect(within(loeslatelseKort).getByRole('heading', { level: 3 }).textContent).toBe(
			'Løslatelse',
		)
		expect(within(innsettelseKort).getByRole('heading', { level: 3 }).textContent).toBe(
			'Innsettelse',
		)
		expect(within(annullertKort).getByText(/^1 melding · sist publisert /)).toBeTruthy()
		expect(within(innsettelseKort).getByText(/^2 meldinger · sist publisert /)).toBeTruthy()
	})

	it('should sort meldinger within hendelse by publiseringstidspunkt', async () => {
		render(<TestHarness />)

		const { innsettelseKort } = await hentKort()
		await aapneKort(innsettelseKort)

		const tidspunkter = within(innsettelseKort)
			.getAllByLabelText('Publiseringstidspunkt')
			.map((felt) => (felt as HTMLInputElement).value)
		expect(tidspunkter).toEqual(['16.06.2026 17:11:30', '18.06.2026 09:00:00'])
		expect(within(innsettelseKort).getAllByText('Eksisterende')).toHaveLength(2)
	})

	it('should mark hendelse as annullert only when all meldinger are annullert', async () => {
		render(<TestHarness />)

		const { annullertKort, innsettelseKort } = await hentKort()
		expect(within(annullertKort).getByText('Annullert')).toBeTruthy()
		expect(within(innsettelseKort).queryByText('Annullert')).toBeNull()
	})

	it('should remove annullert status and show redigering when annullert hendelse is redigert', async () => {
		render(<TestHarness />)

		const { annullertKort } = await hentKort()
		await aapneKort(annullertKort)
		expect(within(annullertKort).getAllByText('Annullert')).toHaveLength(2)
		expect(within(annullertKort).getByText('Publiseringstidspunkt annullering')).toBeTruthy()

		await userEvent.click(within(annullertKort).getByRole('button', { name: /Rediger melding/ }))

		expect(within(annullertKort).getAllByText('Annullert')).toHaveLength(1)
		expect(within(annullertKort).getByText('Redigert')).toBeTruthy()
		expect(within(annullertKort).getByText('Redigering')).toBeTruthy()
		expect(within(annullertKort).getByText('Ny')).toBeTruthy()
	})

	it('should add redigering one second after the latest melding in hendelse', async () => {
		let form: UseFormReturn | undefined
		render(<TestHarness onFormMethods={(formMethods) => (form = formMethods)} />)

		const { innsettelseKort } = await hentKort()
		await aapneKort(innsettelseKort)
		await userEvent.click(within(innsettelseKort).getByRole('button', { name: /Rediger melding/ }))

		const redigering = form?.getValues('instdataKdi.innsettelse')[3]
		expect(redigering.meldingId).toBeNull()
		expect(redigering.hendelseId).toBe('hendelse-aaaaaa')
		expect(redigering.publiseringstidspunkt).toBe('2026-06-18T09:00:01')
	})

	it('should set publiseringstidspunkt to now for new hendelse when now is after latest melding', async () => {
		let form: UseFormReturn | undefined
		render(<TestHarness onFormMethods={(formMethods) => (form = formMethods)} />)
		const foerLeggTil = Date.now() - 1000

		await userEvent.click(await screen.findByRole('button', { name: 'Innsettelse' }))

		const nyHendelse = form?.getValues('instdataKdi.innsettelse')[3]
		expect(publiseringstidspunktTid(nyHendelse.publiseringstidspunkt)).toBeGreaterThanOrEqual(
			foerLeggTil,
		)
	})

	it('should set publiseringstidspunkt one second after latest melding when latest melding is in the future', async () => {
		let form: UseFormReturn | undefined
		render(
			<TestHarness
				onFormMethods={(formMethods) => (form = formMethods)}
				instdataKdi={{
					innsettelse: [melding('m1', 'hendelse-aaaaaa', '2099-01-01T12:00:00')],
					loeslatelse: [],
					annullering: [],
				}}
			/>,
		)

		await userEvent.click(await screen.findByRole('button', { name: 'Løslatelse' }))

		const nyHendelse = form?.getValues('instdataKdi.loeslatelse')[0]
		expect(nyHendelse.publiseringstidspunkt).toBe('2099-01-01T12:00:01')
	})
})

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { vi } from 'vitest'
import React from 'react'
import { HendelseIdDataVisning } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/HendelseIdDataVisning'
import { useHendelseId } from '@/utils/hooks/useHendelseId'

vi.mock('@/utils/hooks/useHendelseId', () => ({
	useHendelseId: vi.fn(),
}))

vi.mock('@/utils/hooks/useRedux', () => ({
	useReduxDispatch: () => vi.fn(),
	useReduxSelector: vi.fn(),
}))

vi.mock('@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning', () => ({
	ApiFeilmelding: ({ feil }: { feil: string }) => <div role="alert">{feil}</div>,
	PdlDataVisning: () => null,
}))

vi.mock('@/components/ui/button/DollyTooltip', () => ({
	default: ({ children, content }: { children: React.ReactNode; content: React.ReactNode }) => (
		<div>
			{children}
			<div data-testid="tooltip-content">{content}</div>
		</div>
	),
}))

vi.mock('@/components/ui/loading/Loading', () => ({
	default: ({ label }: { label: string }) => <div>{label}</div>,
}))

const mockUseHendelseId = vi.mocked(useHendelseId)

const makeMainResponse = (ordrer: unknown[]) => ({
	hovedperson: { ordrer },
	relasjoner: [],
})

const makeRelatertResponse = (ordrer: unknown[]) => ({
	ident: '11111111111',
	ordrer,
})

describe('HendelseIdDataVisning', () => {
	beforeEach(() => {
		mockUseHendelseId.mockReturnValue({ data: undefined, loading: false, error: undefined })
	})

	it('should render nothing when ident is empty', () => {
		const { container } = render(<HendelseIdDataVisning ident="" />)
		expect(container).toBeEmptyDOMElement()
	})

	it('should render ident trigger button when ident is provided', () => {
		render(<HendelseIdDataVisning ident="12345678901" />)
		expect(screen.getByText('12345678901 (HOVEDPERSON)')).toBeInTheDocument()
	})

	it('should render one trigger button per related person', () => {
		render(
			<HendelseIdDataVisning
				ident="12345678901"
				relatertePersoner={[
					{ type: 'BARN', id: '11111111111' },
					{ type: 'PARTNER', id: '22222222222' },
				]}
			/>,
		)
		expect(screen.getByText('12345678901 (HOVEDPERSON)')).toBeInTheDocument()
		expect(screen.getByText('11111111111 (BARN)')).toBeInTheDocument()
		expect(screen.getByText('22222222222 (PARTNER)')).toBeInTheDocument()
	})

	it('should show loading state in tooltip content', () => {
		mockUseHendelseId.mockReturnValue({ data: undefined, loading: true, error: undefined })
		render(<HendelseIdDataVisning ident="12345678901" />)
		expect(screen.getByText('Laster hendelse-IDer')).toBeInTheDocument()
	})

	it('should show error state in tooltip content', () => {
		mockUseHendelseId.mockReturnValue({
			data: undefined,
			loading: false,
			error: new Error('Noe gikk galt'),
		})
		render(<HendelseIdDataVisning ident="12345678901" />)
		expect(screen.getByRole('alert')).toHaveTextContent('Noe gikk galt')
	})

	it('should show imported-relation warning when no ordrer exist', async () => {
		mockUseHendelseId.mockReturnValue({
			data: makeRelatertResponse([]),
			loading: false,
			error: undefined,
		})
		render(
			<HendelseIdDataVisning
				ident="12345678901"
				relatertePersoner={[{ type: 'BARN', id: '11111111111' }]}
			/>,
		)
		expect(screen.getAllByText(/importert/i).length).toBeGreaterThan(0)
		expect(screen.getAllByText(/hovedperson/i).length).toBeGreaterThan(0)
		expect(screen.queryByText('11111111111 (BARN)')).not.toBeInTheDocument()
		await waitFor(() =>
			expect(screen.queryByText('12345678901 (HOVEDPERSON)')).not.toBeInTheDocument(),
		)
	})

	it('should render one accordion header per infoElement for main person', () => {
		mockUseHendelseId.mockReturnValue({
			data: makeMainResponse([
				{
					infoElement: 'PDL_DOEDSFALL',
					hendelser: [{ id: 1, status: 'OK', hendelseId: 'abc-123' }],
				},
				{ infoElement: 'PDL_NAVN', hendelser: [{ id: 2, status: 'OK', hendelseId: 'def-456' }] },
			]),
			loading: false,
			error: undefined,
		})
		render(<HendelseIdDataVisning ident="12345678901" />)
		fireEvent.click(screen.getByRole('button', { name: /dødsfall/i }))
		fireEvent.click(screen.getByRole('button', { name: /navn/i }))
		expect(screen.getByText('abc-123')).toBeInTheDocument()
		expect(screen.getByText('def-456')).toBeInTheDocument()
	})

	it('should sort accordion categories alphabetically', () => {
		mockUseHendelseId.mockReturnValue({
			data: makeMainResponse([
				{ infoElement: 'PDL_ZETA', hendelser: [{ id: 1, status: 'OK', hendelseId: 'z' }] },
				{ infoElement: 'PDL_ALFA', hendelser: [{ id: 2, status: 'OK', hendelseId: 'a' }] },
				{ infoElement: 'PDL_MIDT', hendelser: [{ id: 3, status: 'OK', hendelseId: 'm' }] },
			]),
			loading: false,
			error: undefined,
		})
		render(<HendelseIdDataVisning ident="12345678901" />)
		const accordionHeaders = screen
			.getAllByRole('button')
			.filter((element) => element.className.includes('aksel-accordion__header'))
			.map((element) => element.textContent ?? '')
		expect(accordionHeaders.some((text) => text.toLowerCase().includes('alfa'))).toBe(true)
		expect(accordionHeaders.some((text) => text.toLowerCase().includes('midt'))).toBe(true)
		expect(accordionHeaders.some((text) => text.toLowerCase().includes('zeta'))).toBe(true)
		expect(accordionHeaders.findIndex((text) => text.toLowerCase().includes('alfa'))).toBeLessThan(
			accordionHeaders.findIndex((text) => text.toLowerCase().includes('midt')),
		)
		expect(accordionHeaders.findIndex((text) => text.toLowerCase().includes('midt'))).toBeLessThan(
			accordionHeaders.findIndex((text) => text.toLowerCase().includes('zeta')),
		)
	})

	it('should render hendelseId with copy button and no status label', () => {
		mockUseHendelseId.mockReturnValue({
			data: makeMainResponse([
				{
					infoElement: 'PDL_DOEDSFALL',
					hendelser: [{ id: 1, status: 'OK', hendelseId: 'abc-123' }],
				},
			]),
			loading: false,
			error: undefined,
		})
		render(<HendelseIdDataVisning ident="12345678901" />)
		fireEvent.click(screen.getByRole('button', { name: /dødsfall/i }))
		expect(screen.getByText('abc-123')).toBeInTheDocument()
		expect(screen.getByTitle('Kopier hendelse-ID')).toBeInTheDocument()
		expect(screen.queryByText('status')).not.toBeInTheDocument()
		expect(screen.queryByText('PDL_DOEDSFALL')).not.toBeInTheDocument()
	})

	it('should render one copy button per hendelse in the same category', () => {
		mockUseHendelseId.mockReturnValue({
			data: makeMainResponse([
				{
					infoElement: 'PDL_NAVN',
					hendelser: [
						{ id: 1, status: 'OK', hendelseId: 'abc-1' },
						{ id: 2, status: 'OK', hendelseId: 'abc-2' },
					],
				},
			]),
			loading: false,
			error: undefined,
		})
		render(<HendelseIdDataVisning ident="12345678901" />)
		fireEvent.click(screen.getByRole('button', { name: /navn/i }))
		expect(screen.getByText('abc-1')).toBeInTheDocument()
		expect(screen.getByText('abc-2')).toBeInTheDocument()
		expect(screen.getAllByTitle('Kopier hendelse-ID')).toHaveLength(2)
		expect(screen.getByLabelText('2 hendelser')).toBeInTheDocument()
	})

	it('should use data.ordrer for relatert ident', () => {
		mockUseHendelseId.mockReturnValue({
			data: makeRelatertResponse([
				{
					infoElement: 'PDL_FORELDRE_BARN_RELASJON',
					hendelser: [{ id: 7, status: 'OK', hendelseId: 'rel-789' }],
				},
			]),
			loading: false,
			error: undefined,
		})
		render(
			<HendelseIdDataVisning
				ident="12345678901"
				relatertePersoner={[{ type: 'BARN', id: '11111111111' }]}
			/>,
		)
		fireEvent.click(screen.getByRole('button', { name: /foreldre/i }))
		expect(screen.getByText('rel-789')).toBeInTheDocument()
	})

	it('should group repeated infoElements into one category', () => {
		mockUseHendelseId.mockReturnValue({
			data: makeMainResponse([
				{
					infoElement: 'PDL_SIVILSTAND',
					hendelser: [{ id: 1, status: 'OK', hendelseId: 'a' }],
				},
				{
					infoElement: 'PDL_SIVILSTAND',
					hendelser: [{ id: 2, status: 'OK', hendelseId: 'b' }],
				},
			]),
			loading: false,
			error: undefined,
		})
		render(<HendelseIdDataVisning ident="12345678901" />)
		fireEvent.click(screen.getByRole('button', { name: /sivilstand/i }))
		expect(screen.getByText('a')).toBeInTheDocument()
		expect(screen.getByText('b')).toBeInTheDocument()
		expect(screen.getByLabelText('2 hendelser')).toBeInTheDocument()
	})

	it('should hide PDL_SLETTING and PDL_OPPRETT_PERSON when status is OK', () => {
		mockUseHendelseId.mockReturnValue({
			data: makeMainResponse([
				{ infoElement: 'PDL_SLETTING', hendelser: [{ status: 'OK' }] },
				{ infoElement: 'PDL_OPPRETT_PERSON', hendelser: [{ status: 'OK' }] },
				{ infoElement: 'PDL_NAVN', hendelser: [{ id: 1, status: 'OK', hendelseId: 'navn-1' }] },
			]),
			loading: false,
			error: undefined,
		})
		render(<HendelseIdDataVisning ident="12345678901" />)
		expect(screen.queryByRole('button', { name: /sletting/i })).not.toBeInTheDocument()
		expect(screen.queryByRole('button', { name: /opprett/i })).not.toBeInTheDocument()
		expect(screen.getByRole('button', { name: /navn/i })).toBeInTheDocument()
	})

	it('should show PDL_SLETTING with red category and error text when it has an error', () => {
		mockUseHendelseId.mockReturnValue({
			data: makeMainResponse([
				{
					infoElement: 'PDL_SLETTING',
					hendelser: [
						{ status: 'FEIL', error: 'Mottaker svarer ikke, eller har for lang svartid.' },
					],
				},
			]),
			loading: false,
			error: undefined,
		})
		const { container } = render(<HendelseIdDataVisning ident="12345678901" />)
		const slettingHeader = screen.getByRole('button', { name: /sletting/i })
		expect(slettingHeader).toBeInTheDocument()
		expect(container.querySelector('.hendelse-kategori-feil')).toBeInTheDocument()
		fireEvent.click(slettingHeader)
		expect(
			screen.getByText('Mottaker svarer ikke, eller har for lang svartid.'),
		).toBeInTheDocument()
	})

	it('should show info message when every category is filtered out', () => {
		mockUseHendelseId.mockReturnValue({
			data: makeMainResponse([
				{ infoElement: 'PDL_SLETTING', hendelser: [{ status: 'OK' }] },
				{ infoElement: 'PDL_OPPRETT_PERSON', hendelser: [{ status: 'OK' }] },
			]),
			loading: false,
			error: undefined,
		})
		render(<HendelseIdDataVisning ident="12345678901" />)
		expect(screen.getByText(/Ingen hendelser å vise/i)).toBeInTheDocument()
	})

	it('should pass relatertIdent to hook for related persons', () => {
		render(
			<HendelseIdDataVisning
				ident="12345678901"
				relatertePersoner={[{ type: 'BARN', id: '11111111111' }]}
			/>,
		)
		expect(mockUseHendelseId).toHaveBeenCalledWith('12345678901', undefined)
		expect(mockUseHendelseId).toHaveBeenCalledWith('12345678901', '11111111111')
	})
})

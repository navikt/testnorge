import { act, fireEvent, render, screen, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { SoekTypeValg } from '@/pages/gruppeOversikt/NavigeringTypes'
import Navigering from '@/pages/gruppeOversikt/Navigering'

const mocks = vi.hoisted(() => ({
	dispatch: vi.fn(),
	searchPerson: vi.fn(),
	selectPerson: vi.fn(),
	resetPersonError: vi.fn(),
	searchBestilling: vi.fn(),
	selectBestilling: vi.fn(),
	searchGruppe: vi.fn(),
	selectGruppe: vi.fn(),
}))

vi.mock('react-select', () => ({
	default: ({
		inputValue,
		options,
		onInputChange,
		onChange,
		onMenuScrollToBottom,
		noOptionsMessage,
	}: {
		inputValue: string
		options: Array<{
			options: Array<{ value: string; label: string; type: SoekTypeValg }>
		}>
		onInputChange: (value: string, meta: { action: 'input-change' }) => void
		onChange: (option: { value: string; label: string; type: SoekTypeValg }) => void
		onMenuScrollToBottom: () => void
		noOptionsMessage: () => string
	}) => {
		const visibleOptions = options.flatMap((group) => group.options)
		return (
			<div>
				<input
					aria-label="Person"
					value={inputValue}
					onChange={(event) => onInputChange(event.target.value, { action: 'input-change' })}
				/>
				{visibleOptions.map((option) => (
					<span key={option.value}>{option.label}</span>
				))}
				{visibleOptions.length === 0 && <span>{noOptionsMessage()}</span>}
				{visibleOptions[0] && (
					<button type="button" onClick={() => onChange(visibleOptions[0])}>
						Velg første
					</button>
				)}
				<button type="button" onClick={onMenuScrollToBottom}>
					Last flere
				</button>
			</div>
		)
	},
	components: {
		Option: ({ children }: { children: React.ReactNode }) => <>{children}</>,
		DropdownIndicator: ({ children }: { children: React.ReactNode }) => <>{children}</>,
	},
	createFilter: () => () => true,
}))

vi.mock('@/utils/hooks/useRedux', () => ({
	useReduxDispatch: () => mocks.dispatch,
	useReduxSelector: vi.fn(),
}))

vi.mock('@/ducks/finnPerson', () => ({
	resetFeilmelding: vi.fn(),
}))

vi.mock('@/utils/hooks/useSearchHotkey', () => ({
	useSearchHotkey: () => 'Ctrl-K',
}))

vi.mock('@/pages/gruppeOversikt/FinnPerson', () => ({
	usePersonSearch: () => ({
		search: mocks.searchPerson,
		handleSelect: mocks.selectPerson,
		feilmelding: null,
		resetError: mocks.resetPersonError,
	}),
}))

vi.mock('@/pages/gruppeOversikt/FinnBestilling', () => ({
	useBestillingSearch: () => ({
		search: mocks.searchBestilling,
		handleSelect: mocks.selectBestilling,
	}),
}))

vi.mock('@/pages/gruppeOversikt/FinnGruppe', () => ({
	useGruppeSearch: () => ({
		search: mocks.searchGruppe,
		handleSelect: mocks.selectGruppe,
	}),
}))

describe('Navigering', () => {
	beforeEach(() => {
		vi.clearAllMocks()
		mocks.searchPerson
			.mockResolvedValueOnce({
				group: {
					label: 'Personer',
					options: [{ value: '1', label: '1 - UKJENT', type: SoekTypeValg.PERSON }],
				},
				hasMorePdlf: true,
				hasMorePdl: true,
			})
			.mockResolvedValueOnce({
				group: {
					label: 'Personer',
					options: [
						{ value: '1', label: '1 - OLA NORDMANN', type: SoekTypeValg.PERSON },
						{ value: '2', label: '2 - KARI NORDMANN', type: SoekTypeValg.PERSON },
					],
				},
				hasMorePdlf: false,
				hasMorePdl: false,
			})
		mocks.searchBestilling.mockResolvedValue({ label: 'Bestillinger', options: [] })
		mocks.searchGruppe.mockResolvedValue({ label: 'Grupper', options: [] })
	})

	it('waits for three characters and appends the next ident page at the bottom', async () => {
		render(<Navigering />)

		const input = screen.getByRole('textbox', { name: 'Person' })
		fireEvent.change(input, { target: { value: 'ab' } })

		expect(screen.getByText('Skriv minst 3 tegn')).toBeInTheDocument()
		expect(mocks.searchPerson).not.toHaveBeenCalled()
		expect(mocks.searchBestilling).not.toHaveBeenCalled()
		expect(mocks.searchGruppe).not.toHaveBeenCalled()

		fireEvent.change(input, { target: { value: 'abc' } })

		await waitFor(() => expect(mocks.searchPerson).toHaveBeenCalledTimes(1))
		expect(mocks.searchBestilling).toHaveBeenCalledTimes(1)
		expect(mocks.searchGruppe).toHaveBeenCalledTimes(1)

		fireEvent.click(screen.getByRole('button', { name: 'Last flere' }))

		await waitFor(() => expect(mocks.searchPerson).toHaveBeenCalledTimes(2))
		expect(mocks.searchPerson).toHaveBeenLastCalledWith('abc', 1, expect.any(Number), {
			pdlf: true,
			pdl: true,
			pdlAktoer: false,
		})
		expect(mocks.searchBestilling).toHaveBeenCalledTimes(1)
		expect(mocks.searchGruppe).toHaveBeenCalledTimes(1)
		expect(screen.queryByText('1 - UKJENT')).not.toBeInTheDocument()
		expect(screen.getByText('1 - OLA NORDMANN')).toBeInTheDocument()
		expect(screen.getByText('2 - KARI NORDMANN')).toBeInTheDocument()

		fireEvent.click(screen.getByRole('button', { name: 'Velg første' }))

		expect(mocks.selectPerson).toHaveBeenCalledWith('1')
		expect(input).toHaveValue('')
		expect(screen.queryByText('1 - OLA NORDMANN')).not.toBeInTheDocument()
	})

	it('ignores results from an earlier search', async () => {
		let resolveFirstSearch!: (value: {
			group: {
				label: string
				options: Array<{ value: string; label: string; type: SoekTypeValg }>
			}
			hasMorePdlf: boolean
			hasMorePdl: boolean
		}) => void
		const firstSearch = new Promise<Parameters<typeof resolveFirstSearch>[0]>((resolve) => {
			resolveFirstSearch = resolve
		})
		mocks.searchPerson
			.mockReset()
			.mockReturnValueOnce(firstSearch)
			.mockResolvedValueOnce({
				group: {
					label: 'Personer',
					options: [{ value: '2', label: '2 - NYTT SØK', type: SoekTypeValg.PERSON }],
				},
				hasMorePdlf: false,
				hasMorePdl: false,
			})

		render(<Navigering />)
		const input = screen.getByRole('textbox', { name: 'Person' })

		fireEvent.change(input, { target: { value: 'abc' } })
		await waitFor(() => expect(mocks.searchPerson).toHaveBeenCalledTimes(1))
		fireEvent.change(input, { target: { value: 'abd' } })

		await waitFor(() => expect(screen.getByText('2 - NYTT SØK')).toBeInTheDocument())

		await act(async () => {
			resolveFirstSearch({
				group: {
					label: 'Personer',
					options: [{ value: '1', label: '1 - GAMMELT SØK', type: SoekTypeValg.PERSON }],
				},
				hasMorePdlf: false,
				hasMorePdl: false,
			})
		})

		expect(screen.queryByText('1 - GAMMELT SØK')).not.toBeInTheDocument()
		expect(screen.getByText('2 - NYTT SØK')).toBeInTheDocument()
	})
})

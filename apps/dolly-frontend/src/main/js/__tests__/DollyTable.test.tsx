import { fireEvent, render, screen } from '@testing-library/react'
import { dollyTest } from '../vitest.setup'
import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import { MemoryRouter } from 'react-router'
import { Provider } from 'react-redux'
import { configureStore } from '@reduxjs/toolkit'
import { vi } from 'vitest'
import { act } from 'react'

vi.mock('@/pages/gruppe/PersonVisning/PersonVisningConnector', () => ({
	default: () => <div data-testid="person-visning">Person Details</div>,
}))

const mockPersonData = [
	{
		identNr: '12811212312',
		navn: 'Test Person',
		alder: '30',
		bestillingId: ['1'],
		status: 'Ferdig',
		kilde: 'PDL',
		kjonn: 'MANN',
		ident: {
			ident: '12811212312',
			master: 'PDL',
			bestillingId: ['1'],
		},
	},
	{
		identNr: '23423423423',
		navn: 'Another Person',
		alder: '25',
		bestillingId: ['2'],
		status: 'Avvik',
		kilde: 'PDL',
		kjonn: 'KVINNE',
		ident: {
			ident: '23423423423',
			master: 'PDL',
			bestillingId: ['2'],
		},
	},
]

const mockColumns = [
	{
		text: 'Ident',
		width: '25',
		dataField: 'identNr',
		unique: true,
		formatter: (_cell, row) => <span>{row.identNr}</span>,
	},
	{
		text: 'Navn',
		width: '40',
		dataField: 'navn',
	},
	{
		text: 'Alder',
		width: '15',
		dataField: 'alder',
	},
	{
		text: 'Status',
		width: '10',
		dataField: 'status',
	},
]

const createMockStore = (state = {}) =>
	configureStore({
		reducer: () => ({
			...state,
		}),
		preloadedState: state,
	})

dollyTest('renders DollyTable with correct data and columns', () => {
	const mockStore = createMockStore()
	const onHeaderClickMock = vi.fn()

	render(
		<MemoryRouter>
			<Provider store={mockStore}>
				<DollyTable
					data={mockPersonData}
					columns={mockColumns}
					pagination
					iconItem={(bruker) => {
						if (bruker.kjonn === 'MANN' || bruker.kjonn === 'GUTT') {
							return <div data-testid="man-icon">Man Icon</div>
						} else if (bruker.kjonn === 'KVINNE' || bruker.kjonn === 'JENTE') {
							return <div data-testid="woman-icon">Woman Icon</div>
						} else {
							return <div data-testid="unknown-icon">Unknown Icon</div>
						}
					}}
					onExpand={(bruker) => <div data-testid="expanded-content">{bruker.identNr}</div>}
					onHeaderClick={onHeaderClickMock}
					gruppeDetaljer={{
						antallElementer: 2,
						pageSize: 10,
						sidetall: 0,
					}}
				/>
			</Provider>
		</MemoryRouter>,
	)

	expect(screen.getByText('Test Person')).toBeInTheDocument()
	expect(screen.getByText('Another Person')).toBeInTheDocument()
	expect(screen.getByText('30')).toBeInTheDocument()
	expect(screen.getByText('25')).toBeInTheDocument()

	expect(screen.getByText('Ident')).toBeInTheDocument()
	expect(screen.getByText('Navn')).toBeInTheDocument()
	expect(screen.getByText('Alder')).toBeInTheDocument()
	expect(screen.getByText('Status')).toBeInTheDocument()
})

dollyTest('shows correct icons based on gender', () => {
	const mockStore = createMockStore()

	render(
		<MemoryRouter>
			<Provider store={mockStore}>
				<DollyTable
					data={mockPersonData}
					columns={mockColumns}
					pagination
					iconItem={(bruker) => {
						if (bruker.kjonn === 'MANN' || bruker.kjonn === 'GUTT') {
							return <div data-testid="man-icon">Man Icon</div>
						} else if (bruker.kjonn === 'KVINNE' || bruker.kjonn === 'JENTE') {
							return <div data-testid="woman-icon">Woman Icon</div>
						} else {
							return <div data-testid="unknown-icon">Unknown Icon</div>
						}
					}}
					onExpand={() => <div>Expanded Content</div>}
					onHeaderClick={vi.fn()}
					gruppeDetaljer={{
						antallElementer: 2,
						pageSize: 10,
					}}
				/>
			</Provider>
		</MemoryRouter>,
	)

	const manIcons = screen.getAllByTestId('man-icon')
	const womanIcons = screen.getAllByTestId('woman-icon')

	expect(manIcons.length).toBe(1)
	expect(womanIcons.length).toBe(1)
})

dollyTest('calls onHeaderClick when header is clicked', () => {
	const mockStore = createMockStore()
	const onHeaderClickMock = vi.fn()

	render(
		<MemoryRouter>
			<Provider store={mockStore}>
				<DollyTable
					data={mockPersonData}
					columns={mockColumns}
					pagination
					iconItem={() => <div>Icon</div>}
					onExpand={() => <div>Expanded Content</div>}
					onHeaderClick={onHeaderClickMock}
					gruppeDetaljer={{
						antallElementer: 2,
						pageSize: 10,
					}}
				/>
			</Provider>
		</MemoryRouter>,
	)

	const nameHeader = screen.getByText('Navn')

	act(() => {
		fireEvent.click(nameHeader)
	})

	expect(onHeaderClickMock).toHaveBeenCalledWith('Navn')
})

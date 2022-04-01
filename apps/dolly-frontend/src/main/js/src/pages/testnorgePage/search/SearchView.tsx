import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import { ManIconItem, WomanIconItem } from '~/components/ui/icon/IconItem'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import styled from 'styled-components'
import { PersonView } from '~/pages/testnorgePage/search/PersonView'
import { Person } from '~/service/services/personsearch/types'
import Button from '~/components/ui/button/Button'
import { VelgPerson } from '~/pages/testnorgePage/search/VelgPerson'
import './SearchView.less'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Loading from '~/components/ui/loading/Loading'

type Props = {
	items?: Person[]
	loading: boolean
	valgtePersoner: string[]
	setValgtePersoner: (personer: string[]) => void
	importerPersoner: (valgtePersoner: string[]) => void
}

const SearchView = styled.div`
	display: flex;
	flex-direction: column;
`

const getAlder = (foedselsdato: string, doedsdato: string) => {
	const fdato = new Date(foedselsdato)
	let diff_ms = Date.now() - fdato.getTime()
	if (doedsdato !== null && doedsdato !== '') {
		const ddato = new Date(doedsdato)
		diff_ms = ddato.getTime() - fdato.getTime()
	}

	const age_dt = new Date(diff_ms)
	const alder = Math.abs(age_dt.getUTCFullYear() - 1970)
	return <>{alder}</>
}

export default ({ items, loading, valgtePersoner, setValgtePersoner, importerPersoner }: Props) => {
	if (loading) return <Loading label="Søker..." />
	if (!items || items.length === 0) {
		return (
			<ContentContainer>
				Ingen resultat. Resultatet inkluderer ikke identer som allerede er importert til Dolly.
			</ContentContainer>
		)
	}

	const columns = [
		{
			text: 'Ident',
			width: '20',
			dataField: 'ident',
			unique: true,
		},
		{
			text: 'Fornavn',
			width: '20',
			dataField: 'fornavn',
			unique: true,
		},
		{
			text: 'Etternavn',
			width: '20',
			dataField: 'etternavn',
			unique: true,
		},
		{
			text: 'Kjønn',
			width: '10',
			formatter: (cell: any, row: Person) => {
				return <>{row.kjoenn.substring(0, 1)}</>
			},
		},
		{
			text: 'Alder',
			width: '15',
			formatter: (cell: any, row: Person) => {
				const doedsdato = row.doedsfall.doedsdato
				const alder = getAlder(row.foedsel.foedselsdato, doedsdato)
				if (doedsdato != null && doedsdato !== '') {
					return <>{alder}(Død)</>
				}
				return <>{alder}</>
			},
		},
		{
			text: 'Velg alle',
			width: '15',
			dataField: 'velg',
			headerFormatter: (text: string, data: Array<Person>) => {
				return (
					<Button
						onClick={() => {
							const alleValgtPaaSiden = data.every((person) =>
								valgtePersoner.includes(person.ident)
							)
							alleValgtPaaSiden
								? setValgtePersoner([])
								: setValgtePersoner(data.map((person) => person.ident))
						}}
					>
						{text}
					</Button>
				)
			},
			formatter: (cell: any, row: Person) => (
				<VelgPerson
					personinfo={row}
					valgtePersoner={valgtePersoner}
					setValgtePersoner={setValgtePersoner}
				/>
			),
		},
	]
	const personerValgt = valgtePersoner.length > 0

	const onImport = () => {
		importerPersoner(valgtePersoner)
	}

	return (
		<SearchView>
			{/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
			{/*@ts-ignore*/}
			<DollyTable
				data={items}
				columns={columns}
				iconItem={(person: Person) =>
					person?.kjoenn === 'MANN' ? <ManIconItem /> : <WomanIconItem />
				}
				onExpand={(person: Person) => <PersonView person={person} />}
				pagination
			/>
			<div className="flexbox--align-center--justify-end">
				<NavButton
					type="hoved"
					onClick={onImport}
					disabled={!personerValgt}
					title={!personerValgt ? 'Velg personer' : null}
				>
					Importer
				</NavButton>
			</div>
		</SearchView>
	)
}

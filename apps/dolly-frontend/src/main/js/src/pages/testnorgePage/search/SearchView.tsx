import React from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import { ManIconItem, WomanIconItem } from '~/components/ui/icon/IconItem'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import styled from 'styled-components'
import Button from '~/components/ui/button/Button'
import { VelgPerson } from '~/pages/testnorgePage/search/VelgPerson'
import './SearchView.less'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Loading from '~/components/ui/loading/Loading'
import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { getAlder } from '~/ducks/fagsystem'
import Formatters from '~/utils/DataFormatter'
import { getFornavn, getIdent, getEtternavn, getPdlKjoenn } from '~/pages/testnorgePage/utils'
import { PdlVisning } from '~/components/fagsystem/pdl/visning/PdlVisning'
import { CopyButton } from '~/components/ui/button/CopyButton/CopyButton'

type Props = {
	items?: PdlData[]
	loading: boolean
	valgtePersoner: string[]
	setValgtePersoner: (personer: string[]) => void
	importerPersoner: (valgtePersoner: string[]) => void
}

const SearchView = styled.div`
	display: flex;
	flex-direction: column;
`

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
			width: '25',
			formatter: (_cell: any, row: PdlData) => {
				return <CopyButton value={getIdent(row)} />
			},
		},
		{
			text: 'Fornavn',
			width: '20',
			formatter: (_cell: any, row: PdlData) => {
				return <>{getFornavn(row)}</>
			},
		},
		{
			text: 'Etternavn',
			width: '20',
			formatter: (_cell: any, row: PdlData) => {
				return <>{getEtternavn(row)}</>
			},
		},
		{
			text: 'Kjønn',
			width: '10',
			formatter: (_cell: any, row: PdlData) => {
				return <>{getPdlKjoenn(row)}</>
			},
		},
		{
			text: 'Alder',
			width: '15',
			formatter: (_cell: any, row: PdlData) => {
				const alder = getAlder(
					row.hentPerson?.foedsel[0]?.foedselsdato,
					row.hentPerson?.doedsfall[0]?.doedsdato
				)
				return <>{Formatters.formatAlder(alder, row.hentPerson?.doedsfall[0]?.doedsdato)}</>
			},
		},
		{
			text: 'Velg alle',
			width: '15',
			dataField: 'velg',
			headerFormatter: (text: string, data: Array<PdlData>) => {
				return (
					<Button
						onClick={() => {
							const alleValgtPaaSiden = data.every((person) =>
								valgtePersoner.includes(getIdent(person))
							)
							alleValgtPaaSiden
								? setValgtePersoner([])
								: setValgtePersoner(data.map((person) => getIdent(person)))
						}}
					>
						{text}
					</Button>
				)
			},
			formatter: (_cell: any, row: PdlData) => (
				<VelgPerson
					ident={getIdent(row)}
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
				iconItem={(person: PdlData) =>
					getPdlKjoenn(person) === 'M' ? <ManIconItem /> : <WomanIconItem />
				}
				onExpand={(person: PdlData) => <PdlVisning pdlData={person} />}
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

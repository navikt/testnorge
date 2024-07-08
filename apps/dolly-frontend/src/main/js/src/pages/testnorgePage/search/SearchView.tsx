import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import { ManIconItem, WomanIconItem } from '@/components/ui/icon/IconItem'
import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import styled from 'styled-components'
import Button from '@/components/ui/button/Button'
import { VelgPerson } from '@/pages/testnorgePage/search/VelgPerson'
import Loading from '@/components/ui/loading/Loading'
import { PdlData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { getAlder, getKjoenn } from '@/ducks/fagsystem'
import { formatAlder } from '@/utils/DataFormatter'
import { getPdlIdent } from '@/pages/testnorgePage/utils'
import { PdlVisning } from '@/components/fagsystem/pdl/visning/PdlVisning'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'
import { ImportModal } from '@/pages/testnorgePage/search/importModal/ImportModal'
import { Gruppe } from '@/utils/hooks/useGruppe'
import { ArenaVisning } from '@/components/fagsystem/arena/visning/ArenaVisning'

type Props = {
	items?: PdlData[]
	sidetall: number
	loading: boolean
	valgtePersoner: ImportPerson[]
	setValgtePersoner: (personer: ImportPerson[]) => void
	importerPersoner: (
		valgtePersoner: ImportPerson[],
		mal: any,
		navigate: Function,
		gruppeId?: number,
	) => void
	gruppe?: Gruppe
}

export type ImportPerson = {
	ident: string
	data: PdlData
}

const getImportPerson = (data: PdlData) => {
	const ident = getPdlIdent(data)
	return {
		ident: ident,
		data: data,
	}
}

const getFornavn = (person: PdlData) => {
	const navn = person.hentPerson?.navn.filter((personNavn) => !personNavn.metadata.historisk)
	return navn.length > 0 ? navn[0].fornavn : ''
}

const getEtternavn = (person: PdlData) => {
	const navn = person.hentPerson?.navn.filter((personNavn) => !personNavn.metadata.historisk)
	return navn.length > 0 ? navn[0].etternavn : ''
}

const getPdlKjoenn = (person: PdlData) => {
	return person.hentPerson?.kjoenn[0] ? getKjoenn(person.hentPerson?.kjoenn[0].kjoenn) : 'U'
}

const SearchView = styled.div`
	display: flex;
	flex-direction: column;
`

export default ({
	items,
	loading,
	valgtePersoner,
	setValgtePersoner,
	importerPersoner,
	sidetall,
	gruppe,
}: Props) => {
	if (loading) {
		return <Loading label="Søker..." />
	}
	if (!items || items.length === 0) {
		return (
			<ContentContainer>
				Fant ingen identer. Resultatet inkluderer ikke identer som allerede er importert til Dolly.
			</ContentContainer>
		)
	}

	const columns = [
		{
			text: 'Ident',
			width: '25',
			formatter: (_cell: any, row: PdlData) => {
				const fnr = getPdlIdent(row)
				return (
					<DollyCopyButton displayText={fnr} copyText={fnr} tooltipText={'Kopier fødselsnummer'} />
				)
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
					row.hentPerson?.foedselsdato[0]?.foedselsdato || row.hentPerson?.foedsel[0]?.foedselsdato,
					row.hentPerson?.doedsfall[0]?.doedsdato,
				)
				return <>{formatAlder(alder, row.hentPerson?.doedsfall[0]?.doedsdato)}</>
			},
		},
		{
			text: 'Velg alle',
			width: '10',
			dataField: 'velg',
			headerFormatter: (text: string, data: Array<PdlData>) => {
				return (
					<Button
						onClick={() => {
							const alleValgtPaaSiden = data.every((person) =>
								valgtePersoner
									.map((valgtPerson) => valgtPerson?.ident)
									.includes(getPdlIdent(person)),
							)
							alleValgtPaaSiden
								? setValgtePersoner([])
								: setValgtePersoner(data.map((person) => getImportPerson(person)))
						}}
					>
						{text}
					</Button>
				)
			},
			formatter: (_cell: any, row: PdlData) => (
				<VelgPerson
					ident={getPdlIdent(row)}
					data={row}
					valgtePersoner={valgtePersoner}
					setValgtePersoner={setValgtePersoner}
				/>
			),
		},
	]

	return (
		<SearchView>
			<DollyTable
				visSide={sidetall}
				data={items}
				columns={columns}
				iconItem={(person: PdlData) =>
					getPdlKjoenn(person) === 'M' ? <ManIconItem /> : <WomanIconItem />
				}
				onExpand={(person: PdlData) => (
					<>
						<PdlVisning pdlData={person} />
						<ArenaVisning
							ident={{
								ident: getPdlIdent(person),
								master: 'PDL',
							}}
							data={null}
							bestillinger={[]}
							loading={false}
						/>
					</>
				)}
				pagination="simple"
			/>
			<ImportModal
				valgtePersoner={valgtePersoner}
				importerPersoner={importerPersoner}
				gruppe={gruppe}
			/>
		</SearchView>
	)
}

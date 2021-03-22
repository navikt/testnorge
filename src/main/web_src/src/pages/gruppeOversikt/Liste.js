import React, { Fragment } from 'react'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import FavoriteButtonConnector from '~/components/ui/button/FavoriteButton/FavoriteButtonConnector'
import { GruppeIconItem } from '~/components/ui/icon/IconItem'
import Icon from '~/components/ui/icon/Icon'
import ImporterGrupperConnector from './ImporterGrupperConnector'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export default function Liste({
	items,
	history,
	searchActive,
	isFetching,
	gruppeDetaljer,
	visSide,
	setSidetall,
	setSideStoerrelse
}) {
	if (isFetching) return <Loading label="laster grupper" panel />

	if (!items || !items.length) {
		return (
			<ContentContainer>
				{searchActive ? (
					<p>Søket gav ingen resultater.</p>
				) : (
					<>
						<p>Du har for øyeblikket ingen testdatagrupper på denne brukerkontoen.</p>
						<p>
							Om dette er første gang du bruker din personlige brukerkonto kan du importere
							testdatagrupper Z-brukeren(e) du har benyttet tidligere ved å trykke på knappen
							nedenfor. Du kan når som helst importere testdatagrupper fra Z-brukere via Min side
							øverst til høyre.
						</p>
						<p>For å opprette en ny testdatagruppe, trykk på "Ny gruppe"-knappen over.</p>
						<ImporterGrupperConnector />
					</>
				)}
			</ContentContainer>
		)
	}

	const columns = [
		{
			text: 'ID',
			width: '10',
			dataField: 'id',
			unique: true
		},
		{
			text: 'Navn',
			width: '20',
			dataField: 'navn'
		},
		{
			text: 'Hensikt',
			width: '20',
			dataField: 'hensikt'
		},
		{
			text: 'Personer',
			width: '15',
			dataField: 'antallIdenter'
		},
		{
			text: 'Antall brukt',
			width: '15',
			dataField: 'antallIBruk'
		},
		{
			text: 'Favoritt',
			width: '15',
			dataField: 'id',
			formatter: (cell, row) =>
				!row.erEierAvGruppe && <FavoriteButtonConnector hideLabel={true} groupId={row.id} />
		},
		{
			text: 'Låst',
			width: '10',
			dataField: 'erLaast',
			formatter: (cell, row) => row.erLaast && <Icon kind={'lock'} />
		}
	]
	return (
		<ErrorBoundary>
			<DollyTable
				data={items}
				columns={columns}
				onRowClick={row => () => history.push(`gruppe/${row.id}`)}
				iconItem={<GruppeIconItem />}
				pagination
				gruppeDetaljer={gruppeDetaljer}
				visSide={visSide}
				setSidetall={setSidetall}
				setSideStoerrelse={setSideStoerrelse}
			/>
		</ErrorBoundary>
	)
}

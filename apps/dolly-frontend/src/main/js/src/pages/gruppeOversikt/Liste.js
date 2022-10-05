import React from 'react'
import { DollyTable } from '~/components/ui/dollyTable/DollyTable'
import Loading from '~/components/ui/loading/Loading'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import FavoriteButtonConnector from '~/components/ui/button/FavoriteButton/FavoriteButtonConnector'
import { GruppeIconItem } from '~/components/ui/icon/IconItem'
import Icon from '~/components/ui/icon/Icon'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Formatters from '~/utils/DataFormatter'
import { useNavigate } from 'react-router-dom'

export default function Liste({ items, searchActive, isFetching, gruppeDetaljer, visSide }) {
	const navigate = useNavigate()
	if (isFetching) return <Loading label="Laster grupper" panel />

	if (!items || !items.length) {
		return (
			<ContentContainer>
				{searchActive ? (
					<p>Søket gav ingen resultater.</p>
				) : (
					<>
						<p>Du har for øyeblikket ingen grupper på denne brukerkontoen.</p>
						<p>For å opprette en ny gruppe, trykk på "Ny gruppe"-knappen over.</p>
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
			unique: true,
		},
		{
			text: 'Navn',
			width: '20',
			dataField: 'navn',
		},
		{
			text: 'Hensikt',
			width: '20',
			dataField: 'hensikt',
		},
		{
			text: 'Personer',
			width: '15',
			dataField: 'antallIdenter',
		},
		{
			text: 'Antall brukt',
			width: '15',
			dataField: 'antallIBruk',
		},
		{
			text: 'Favoritt',
			width: '15',
			dataField: 'id',
			formatter: (_cell, row) =>
				!row.erEierAvGruppe && <FavoriteButtonConnector hideLabel={true} groupId={row.id} />,
		},
		{
			text: 'Låst',
			width: '10',
			dataField: 'erLaast',
			formatter: (_cell, row) => row.erLaast && <Icon kind={'lock'} />,
		},
		{
			text: 'Tags',
			width: '25',
			dataField: 'tags',
			formatter: (_cell, row) =>
				Formatters.arrayToString(row.tags?.length > 1 ? [...row.tags].sort() : row.tags),
		},
	]
	return (
		<ErrorBoundary>
			<DollyTable
				pagination
				data={items}
				columns={columns}
				onRowClick={(row) => () => navigate(`/gruppe/${row.id}`)}
				iconItem={<GruppeIconItem />}
				gruppeDetaljer={gruppeDetaljer}
				visSide={visSide}
			/>
		</ErrorBoundary>
	)
}

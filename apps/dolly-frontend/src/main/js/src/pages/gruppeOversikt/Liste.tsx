import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import Loading from '@/components/ui/loading/Loading'
import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import FavoriteButtonConnector from '@/components/ui/button/FavoriteButton/FavoriteButtonConnector'
import { GruppeIconItem } from '@/components/ui/icon/IconItem'
import Icon from '@/components/ui/icon/Icon'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Formatters from '@/utils/DataFormatter'
import { useNavigate } from 'react-router-dom'
import { VisningType } from '@/pages/gruppeOversikt/GruppeOversikt'

export default function Liste({
	items,
	searchActive,
	isFetching,
	gruppeDetaljer,
	visSide,
	visningType = null,
}) {
	const navigate = useNavigate()
	if (isFetching) return <Loading label="Laster grupper" panel />

	const getEmptyStartText = (visningType) => {
		switch (visningType) {
			case VisningType.ALLE:
				return 'Fant ingen grupper.'
			case VisningType.FAVORITTER:
				return 'Fant ingen favoritter.'
			default:
				return 'Du har for øyeblikket ingen grupper på denne brukerkontoen.'
		}
	}

	if (!items || !items.length) {
		return (
			<ContentContainer>
				{searchActive ? (
					<p>Søket gav ingen resultater.</p>
				) : (
					<>
						<p>{getEmptyStartText(visningType)}</p>
						{visningType !== VisningType.FAVORITTER && (
							<p>For å opprette en ny gruppe, trykk på "Ny gruppe"-knappen over.</p>
						)}
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

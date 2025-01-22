import { DollyTable } from '@/components/ui/dollyTable/DollyTable'
import Loading from '@/components/ui/loading/Loading'
import ContentContainer from '@/components/ui/contentContainer/ContentContainer'
import { GruppeIconItem } from '@/components/ui/icon/IconItem'
import Icon from '@/components/ui/icon/Icon'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { arrayToString } from '@/utils/DataFormatter'
import { useNavigate } from 'react-router'
import { VisningType } from '@/pages/gruppeOversikt/GruppeOversikt'
import FavoriteButton from '@/components/ui/button/FavoriteButton/FavoriteButton'
import { resetPaginering } from '@/ducks/finnPerson'
import { useDispatch } from 'react-redux'

export default function Liste({
	items,
	searchActive,
	isFetching,
	gruppeDetaljer,
	visningType = null,
}) {
	const navigate = useNavigate()
	const dispatch = useDispatch()
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
				!row.erEierAvGruppe && <FavoriteButton hideLabel={true} groupId={row.id} />,
		},
		{
			text: 'Låst',
			width: '10',
			dataField: 'erLaast',
			formatter: (_cell, row) => row.erLaast && <Icon kind={'lock'} fontSize={'1.5rem'} />,
		},
		{
			text: 'Tags',
			width: '25',
			dataField: 'tags',
			formatter: (_cell, row) =>
				arrayToString(row.tags?.length > 1 ? [...row.tags].sort() : row.tags),
		},
	]
	return (
		<ErrorBoundary>
			<DollyTable
				pagination
				data={items}
				columns={columns}
				onRowClick={(row) => () => {
					dispatch(resetPaginering())
					return navigate(`/gruppe/${row.id}`)
				}}
				iconItem={<GruppeIconItem />}
				gruppeDetaljer={gruppeDetaljer}
			/>
		</ErrorBoundary>
	)
}

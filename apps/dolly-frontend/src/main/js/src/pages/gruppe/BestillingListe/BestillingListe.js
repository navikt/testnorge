import React from 'react'
import _orderBy from 'lodash/orderBy'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Loading from '~/components/ui/loading/Loading'
import BestillingDetaljer from '~/components/bestilling/detaljer/BestillingDetaljer'
import { BestillingIconItem } from '~/components/ui/icon/IconItem'

import Icon from '~/components/ui/icon/Icon'
import Spinner from '~/components/ui/loading/Spinner'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

export default function BestillingListe({
	bestillinger,
	searchActive,
	isFetchingBestillinger,
	iLaastGruppe,
}) {
	if (isFetchingBestillinger) return <Loading label="Laster bestillinger" panel />
	if (!bestillinger) return null

	if (bestillinger.length === 0) {
		return (
			<ContentContainer>
				{searchActive
					? 'Søket gav ingen resultater.'
					: 'Trykk på opprett personer-knappen for å starte en bestilling.'}
			</ContentContainer>
		)
	}

	const sortedBestillinger = _orderBy(bestillinger, ['id'], ['desc'])

	const columns = [
		{
			text: 'ID',
			width: '15',
			dataField: 'listedata[0]',
			unique: true,
		},
		{
			text: 'Antall personer',
			width: '15',
			dataField: 'listedata[1]',
		},
		{
			text: 'Sist oppdatert',
			width: '20',
			dataField: 'listedata[2]',
		},
		{
			text: 'Miljø',
			width: '30',
			dataField: 'listedata[3]',
		},
		{
			text: 'Status',
			width: '10',
			dataField: 'listedata[4]',
			formatter: (cell) => {
				return cell === 'Pågår' ? (
					<Spinner size={24} />
				) : (
					<Icon kind={ikonTypeMap[cell]} title={cell} />
				)
			},
		},
	]

	return (
		<ErrorBoundary>
			<DollyTable
				data={sortedBestillinger}
				columns={columns}
				iconItem={<BestillingIconItem />}
				onExpand={(bestilling) => (
					<BestillingDetaljer bestilling={bestilling} iLaastGruppe={iLaastGruppe} />
				)}
				pagination
			/>
		</ErrorBoundary>
	)
}

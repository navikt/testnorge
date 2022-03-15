import React, { useEffect } from 'react'
import _orderBy from 'lodash/orderBy'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import Loading from '~/components/ui/loading/Loading'
import BestillingDetaljer from '~/components/bestilling/detaljer/BestillingDetaljer'
import { BestillingIconItem } from '~/components/ui/icon/IconItem'

import Icon from '~/components/ui/icon/Icon'
import Spinner from '~/components/ui/loading/Spinner'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { SEARCH_IDENT } from '~/pages/gruppe/PersonVisning/TidligereBestillinger/TidligereBestillinger'
import { useDispatch } from 'react-redux'
import { resetSearch, setSearchText } from '~/ducks/search'
import Button from '~/components/ui/button/Button'
import styled from 'styled-components'

const ikonTypeMap = {
	Ferdig: 'feedback-check-circle',
	Avvik: 'report-problem-circle',
	Feilet: 'report-problem-triangle',
	Stoppet: 'report-problem-triangle',
}

const FjernFilterButton = styled(Button)`
	margin-bottom: 10px;
	margin-top: -10px;
	color: #890606;

	:hover {
		border-color: #890606;
	}

	svg * {
		fill: #890606;
	}
`

export default function BestillingListe({
	bestillinger,
	searchActive,
	isFetchingBestillinger,
	iLaastGruppe,
	brukertype,
	navigerBestillingId,
}) {
	if (isFetchingBestillinger) return <Loading label="Laster bestillinger" panel />
	if (!bestillinger) return null

	const searchIdent = sessionStorage.getItem(SEARCH_IDENT)
	const searchInfo = `Søket er filtrert etter ident ${searchIdent}, trykk for å fjerne filtreringen`

	if (bestillinger.length === 0) {
		let infoTekst = 'Trykk på opprett personer-knappen for å starte en bestilling.'
		if (searchActive) infoTekst = 'Søket gav ingen resultater.'
		else if (brukertype === 'BANKID')
			infoTekst =
				'Trykk på importer personer-knappen for å kunne søke opp og importere identer til gruppen.'

		return <ContentContainer>{infoTekst}</ContentContainer>
	}

	const dispatch = useDispatch()

	useEffect(() => {
		if (searchIdent) {
			dispatch(setSearchText(searchIdent))
		}
	}, [])

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
			{searchIdent && (
				<div className={'flexbox--justify-center'}>
					<FjernFilterButton
						kind="kryss"
						onClick={() => dispatch(resetSearch())}
						title={searchInfo}
					>
						FJERN FILTRERING
					</FjernFilterButton>
				</div>
			)}
			<DollyTable
				data={sortedBestillinger}
				columns={columns}
				iconItem={<BestillingIconItem />}
				visBestilling={navigerBestillingId}
				onExpand={(bestilling) => (
					<BestillingDetaljer bestilling={bestilling} iLaastGruppe={iLaastGruppe} />
				)}
				pagination
			/>
		</ErrorBoundary>
	)
}

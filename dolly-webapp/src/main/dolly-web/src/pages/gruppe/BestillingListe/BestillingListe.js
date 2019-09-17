import React, { PureComponent } from 'react'
import _orderBy from 'lodash/orderBy'
import Table from '~/components/table/Table'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import PaginationConnector from '~/components/pagination/PaginationConnector'
import Loading from '~/components/loading/Loading'
import BestillingDetaljerConnector from './BestillingDetaljer/BestillingDetaljerConnector'

export default class BestillingListe extends PureComponent {
	componentWillMount() {
		this.props.getEnvironments()
	}

	render() {
		const { bestillinger, searchActive, isFetchingBestillinger } = this.props

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
		return (
			<PaginationConnector
				items={sortedBestillinger}
				render={items => (
					<Table>
						<Table.Header>
							<Table.Column width="15" value="ID" />
							<Table.Column width="15" value="Antall testpersoner" />
							<Table.Column width="20" value="Sist oppdatert" />
							<Table.Column width="30" value="Miljø" />
							<Table.Column width="10" value="Status" />
							<Table.Column width="10" />
						</Table.Header>

						{items.map((bestilling, idx) => {
							const [id, antallIdenter, sistOppdatert, environments, status] = bestilling.listedata
							return (
								<Table.Row
									key={idx}
									expandComponent={<BestillingDetaljerConnector bestilling={bestilling} />}
								>
									<Table.Column width="15" value={id} />
									<Table.Column width="15" value={antallIdenter} />
									<Table.Column width="20" value={sistOppdatert} />
									<Table.Column width="30" value={environments} />
									<Table.Column width="10" value={status} />
								</Table.Row>
							)
						})}
					</Table>
				)}
			/>
		)
	}
}

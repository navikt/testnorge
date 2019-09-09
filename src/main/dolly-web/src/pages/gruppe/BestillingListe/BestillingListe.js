import React, { PureComponent } from 'react'
import _orderBy from 'lodash/orderBy'
import Table from '~/components/table/Table'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import PaginationConnector from '~/components/pagination/PaginationConnector'
import Formatters from '~/utils/DataFormatter'
import BestillingDetaljerConnector from './BestillingDetaljer/BestillingDetaljerConnector'

export default class BestillingListe extends PureComponent {
	componentWillMount() {
		this.props.getEnvironments()
	}

	render() {
		const { bestillinger, searchActive } = this.props
		if (!bestillinger) return null
		const sortedBestillinger = _orderBy(bestillinger, ['id'], ['desc'])

		return (
			<div className="oversikt-container">
				{bestillinger.length <= 0 ? (
					<ContentContainer>
						{searchActive
							? 'Søket gav ingen resultater.'
							: 'Trykk på opprett personer-knappen for å starte en bestilling.'}
					</ContentContainer>
				) : (
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

								{items &&
									items.map((bestilling, idx) => {
										return (
											<Table.Row
												key={idx}
												expandComponent={<BestillingDetaljerConnector bestilling={bestilling} />}
											>
												<Table.Column width="15" value={bestilling.id} />
												<Table.Column width="15" value={bestilling.antallIdenter} />
												<Table.Column width="20" value={bestilling.sistOppdatert} />
												<Table.Column
													width="30"
													value={Formatters.arrayToString(bestilling.environments)}
												/>
												<Table.Column width="10" value={bestilling.ferdig} />
											</Table.Row>
										)
									})}
							</Table>
						)}
					/>
				)}
			</div>
		)
	}
}

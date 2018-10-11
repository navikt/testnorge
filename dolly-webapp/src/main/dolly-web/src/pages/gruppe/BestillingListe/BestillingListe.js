import React, { PureComponent } from 'react'
import Table from '~/components/table/Table'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import Formatters from '~/utils/DataFormatter'
import BestillingDetaljer from './BestillingDetaljer/BestillingDetaljer'

export default class BestillingListe extends PureComponent {
	render() {
		const { bestillinger } = this.props

		return (
			<div className="oversikt-container">
				{bestillinger.length <= 0 ? (
					<ContentContainer>
						Trykk på opprett personer-knappen for å starte en bestilling.
					</ContentContainer>
				) : (
					<Table>
						<Table.Header>
							<Table.Column width="15" value="ID" />
							<Table.Column width="15" value="Antall testpersoner" />
							<Table.Column width="20" value="Sist oppdatert" />
							<Table.Column width="30" value="Miljø" />
							<Table.Column width="10" value="Status" />
						</Table.Header>

						{bestillinger &&
							bestillinger.map((bestilling, idx) => {
								return (
									<Table.Row key={idx} expandComponent={<BestillingDetaljer />}>
										<Table.Column width="15" value={bestilling.id.toString()} />
										<Table.Column width="15" value={bestilling.antallIdenter.toString()} />
										<Table.Column
											width="20"
											value={Formatters.formatDate(bestilling.sistOppdatert)}
										/>
										<Table.Column
											width="30"
											value={Formatters.arrayToString(bestilling.environments)}
										/>
										<Table.Column width="10" value={bestilling.ferdig ? 'Ferdig' : 'Pågår'} />
									</Table.Row>
								)
							})}
					</Table>
				)}
			</div>
		)
	}
}

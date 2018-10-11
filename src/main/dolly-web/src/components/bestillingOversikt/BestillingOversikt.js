import React, { PureComponent, Fragment } from 'react'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import DataFormatter from '~/utils/DataFormatter'
import Knapp from 'nav-frontend-knapper'

import './BestillingOversikt.less'

class BestillingOversikt extends PureComponent {
	render() {
		const { gruppe } = this.props
		return (
			<div className="bestilling-oversikt">
				<h2>BESTILLINGER</h2>
				{gruppe.bestillinger.map(bestilling => {
					return (
						<div className="bestilling-oversikt_item" key={bestilling.id}>
							<StaticValue header="BESTILLINGSID" value={bestilling.id.toString()} />
							<StaticValue
								header="ANTALL TESTIDENTER"
								value={bestilling.antallIdenter.toString()}
							/>
							<StaticValue
								header="SIST OPPDATERT"
								value={DataFormatter.formatDate(bestilling.sistOppdatert)}
							/>
							<StaticValue
								header="MILJØER"
								value={DataFormatter.arrayToString(bestilling.environments)}
							/>
						</div>
					)
				})}
			</div>
		)
	}
}

export default BestillingOversikt

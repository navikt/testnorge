import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import ExpandButton from '~/components/button/ExpandButton'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import BestillingOversiktConnector from '~/components/bestillingOversikt/BestillingOversiktConnector'

import './GruppeDetaljer.less'

export default class GruppeDetaljer extends PureComponent {
	static propTypes = {
		gruppe: PropTypes.object
	}

	state = {
		// TODO: BYTT TILBAKE TIL FALSE
		expanded: true
	}

	toggleExpanded = () => this.setState({ expanded: !this.state.expanded })

	render() {
		const { gruppe } = this.props

		return (
			<div className="gruppe-detaljer">
				<div className="gd-blokker">
					<StaticValue header="EIER" value={gruppe.opprettetAvNavIdent} />
					<StaticValue header="TEAM" value={gruppe.team.navn} />
					<StaticValue
						header="ANTALL OPPRETTEDE TESTPERSONER"
						value={String(gruppe.testidenter.length)}
					/>
					<StaticValue header="SIST ENDRET" value={gruppe.datoEndret} />
					{this.state.expanded && (
						<Fragment>
							<StaticValue header="HENSIKT" value={gruppe.hensikt} />
							<BestillingOversiktConnector gruppe={gruppe} />
						</Fragment>
					)}
				</div>
				<div className="gruppe-detaljer-chevron">
					<ExpandButton onClick={this.toggleExpanded} expanded={this.state.expanded} />
				</div>
			</div>
		)
	}
}

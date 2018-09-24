import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import ExpandButton from '~/components/button/ExpandButton'
import StaticValue from '~/components/fields/StaticValue/StaticValue'

import './GruppeDetaljer.less'

export default class GruppeDetaljer extends PureComponent {
	static propTypes = {
		gruppe: PropTypes.object
	}

	state = {
		expanded: false
	}

	toggleExpanded = () => this.setState({ expanded: !this.state.expanded })

	render() {
		const { gruppe } = this.props

		return (
			<div className="gruppe-detaljer">
				<div className="gd-blokker">
					<StaticValue header={<h2>EIER</h2>} value={gruppe.opprettetAvNavIdent} />
					<StaticValue header={<h2>TEAM</h2>} value={gruppe.team.navn} />
					<StaticValue
						header={<h2>ANTALL OPPRETTEDE TESTPERSONER</h2>}
						value={String(gruppe.testidenter.length)}
					/>
					<StaticValue header={<h2>SIST ENDRET</h2>} value={gruppe.datoEndret} />
					{this.state.expanded && <StaticValue header={<h2>HENSIKT</h2>} value={gruppe.hensikt} />}
				</div>
				<div className="gruppe-detaljer-chevron">
					<ExpandButton onClick={this.toggleExpanded} expanded={this.state.expanded} />
				</div>
			</div>
		)
	}
}

import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import ExpandButton from '~/components/ui/button/ExpandButton'
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
					<StaticValue header="EIER" value={gruppe.opprettetAvNavIdent} />
					<StaticValue header="TEAM" value={gruppe.team.navn} />
					<StaticValue
						header="ANTALL OPPRETTEDE TESTPERSONER"
						value={String(gruppe.testidenter ? gruppe.testidenter.length : 0)}
					/>
					<StaticValue header="SIST ENDRET" value={gruppe.datoEndret} />
					{this.state.expanded && (
						<Fragment>
							<StaticValue header="HENSIKT" value={gruppe.hensikt} />
							{/* <StaticValue header="SENDT TIL OPENAM" value={gruppe.openAmSent ? 'JA' : 'NEI'} /> */}
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

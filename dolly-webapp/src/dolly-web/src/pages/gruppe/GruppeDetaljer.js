import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import IconButton from '~/components/fields/IconButton/IconButton'
import StaticValue from '~/components/fields/StaticValue/StaticValue'

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

		const iconType = this.state.expanded ? 'chevron-up' : 'chevron-down'

		return (
			<div className="gruppe-detaljer">
				<div className="gd-blokker">
					<StaticValue header="EIER" value={gruppe.opprettetAvNavIdent} />
					<StaticValue header="TEAM" value={gruppe.teamTilhoerlighetNavn} />
					<StaticValue header="ANTALL PERSONER" value={String(gruppe.testidenter.length + 1)} />
					<StaticValue header="SIST ENDRET" value={gruppe.datoEndret} />
					{this.state.expanded && <StaticValue header="HENSIKT" value={gruppe.hensikt} />}
				</div>
				<IconButton kind={iconType} onClick={this.toggleExpanded} />
			</div>
		)
	}
}

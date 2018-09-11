import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import GruppeDetaljer from './GruppeDetaljer/GruppeDetaljer'
import BestillingStatus from './BestillingStatus/BestillingStatus'
import Loading from '~/components/loading/Loading'
import TestbrukerListeConnector from './TestbrukerListe/TestbrukerListeConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import AddButton from '~/components/button/AddButton'

import './Gruppe.less'

export default class Gruppe extends Component {
	static propTypes = {
		gruppeArray: PropTypes.array,
		isFetching: PropTypes.bool,
		createOrUpdateId: PropTypes.string
	}

	state = {
		redigerGruppe: false
	}

	componentDidMount() {
		this.props.getGruppe()
	}

	startBestilling = () => {
		const { gruppeId } = this.props.match.params
		this.props.history.push(`/gruppe/${gruppeId}/bestilling`)
	}

	toggleRedigerGruppe = () => this.setState({ redigerGruppe: !this.state.redigerGruppe })

	render() {
		const { gruppeArray, createOrUpdateId, createGroup, isFetching, getGruppe } = this.props

		if (isFetching) return <Loading label="laster gruppe" panel />

		if (!gruppeArray) return null

		const gruppe = gruppeArray[0]

		let groupActions = []

		// Vise redigeringsknapp eller stjerne
		if (gruppe.erMedlemAvTeamSomEierGruppe) {
			groupActions.push({
				icon: 'edit',
				onClick: createGroup
			})
			groupActions.push({
				icon: 'trashcan',
				onClick: () => {
					alert('ikke implementert')
				}
			})
		} else {
			groupActions.push({
				icon: 'star',
				onClick: () => {
					alert('ikke implementert')
				}
			})
		}

		return (
			<div id="gruppe-container">
				<Overskrift label={gruppe.navn} actions={groupActions} />

				{createOrUpdateId && <RedigerGruppeConnector gruppe={gruppe} />}

				<GruppeDetaljer gruppe={gruppe} />

				{gruppe.bestillinger.map(bestilling => (
					<BestillingStatus key={bestilling.id} bestilling={bestilling} onGroupUpdate={getGruppe} />
				))}

				<TestbrukerListeConnector testidenter={gruppe.testidenter} />
				<AddButton title="Opprett ny bestilling" onClick={this.startBestilling} />
			</div>
		)
	}
}

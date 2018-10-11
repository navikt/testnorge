import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import GruppeDetaljer from './GruppeDetaljer/GruppeDetaljer'
import BestillingStatus from './BestillingStatus/BestillingStatus'
import Loading from '~/components/loading/Loading'
import TestbrukerListeConnector from './TestbrukerListe/TestbrukerListeConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'

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
		const {
			gruppeArray,
			createOrUpdateId,
			createGroup,
			isFetching,
			getGruppe,
			deleteGruppe,
			editTestbruker,
			getBestillingStatus
		} = this.props

		if (isFetching) return <Loading label="Laster grupper" panel />

		if (!gruppeArray) return null

		const gruppe = gruppeArray[0]

		let groupActions = []

		// Vise redigeringsknapp eller stjerne
		if (gruppe.erMedlemAvTeamSomEierGruppe) {
			groupActions.push({
				icon: 'edit',
				onClick: createGroup
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
				<Overskrift label={gruppe.navn} actions={groupActions}>
					{/* <ConfirmTooltip onClick={deleteGruppe} /> */}
				</Overskrift>

				{createOrUpdateId && <RedigerGruppeConnector gruppe={gruppe} />}

				<GruppeDetaljer gruppe={gruppe} />

				{gruppe.bestillinger.map(bestilling => (
					<BestillingStatus key={bestilling.id} bestilling={bestilling} onGroupUpdate={getGruppe} />
				))}

				<TestbrukerListeConnector
					startBestilling={this.startBestilling}
					testidenter={gruppe.testidenter}
					editTestbruker={editTestbruker}
				/>
			</div>
		)
	}
}

import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import GruppeDetaljer from './GruppeDetaljer/GruppeDetaljer'
import BestillingStatus from './BestillingStatus/BestillingStatus'
import Loading from '~/components/loading/Loading'
import TestbrukerListeConnector from './TestbrukerListe/TestbrukerListeConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import BestillingListe from './BestillingListe/BestillingListe'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import Toolbar from '~/components/toolbar/Toolbar'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'
import Knapp from 'nav-frontend-knapper'

import './Gruppe.less'

export default class Gruppe extends Component {
	static propTypes = {
		gruppeArray: PropTypes.array,
		isFetching: PropTypes.bool,
		createOrUpdateId: PropTypes.string
	}

	state = {
		redigerGruppe: false,
		visning: 'best'
	}

	componentDidMount() {
		this.props.getGruppe()
	}

	startBestilling = () => {
		const { gruppeId } = this.props.match.params
		this.props.history.push(`/gruppe/${gruppeId}/bestilling`)
	}

	toggleRedigerGruppe = () => this.setState({ redigerGruppe: !this.state.redigerGruppe })

	toggleToolbar = e => {
		const visning = e.target.value
		this.setState({ visning })
	}

	renderList = gruppe => {
		const { visning } = this.state
		const { editTestbruker } = this.props

		if (visning === 'best') return <BestillingListe bestillinger={gruppe.bestillinger} />

		return (
			<TestbrukerListeConnector testidenter={gruppe.testidenter} editTestbruker={editTestbruker} />
		)
	}

	render() {
		const {
			gruppeArray,
			createOrUpdateId,
			createGroup,
			isFetching,
			getGruppe,
			deleteGruppe,
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

		const toggleValues = [
			{ value: 'test', label: `Testpersoner (${gruppe.testidenter.length})` },
			{ value: 'best', label: `Bestillinger (${gruppe.bestillinger.length})` }
		]

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

				<Toolbar
					searchField={SearchFieldConnector}
					toggleOnChange={this.toggleToolbar}
					toggleCurrent={this.state.visning}
					toggleValues={toggleValues}
				>
					<Knapp type="hoved" onClick={this.startBestilling}>
						Opprett personer
					</Knapp>
				</Toolbar>

				{this.renderList(gruppe)}
			</div>
		)
	}
}

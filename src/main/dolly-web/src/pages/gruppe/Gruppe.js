import React, { Component } from 'react'
import Overskrift from '~/components/overskrift/Overskrift'
import GruppeDetaljer from './GruppeDetaljer/GruppeDetaljer'
import BestillingStatus from './BestillingStatus/BestillingStatus'
import Loading from '~/components/loading/Loading'
import TestbrukerListeConnector from './TestbrukerListe/TestbrukerListeConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import AddButton from '~/components/button/AddButton'

import './Gruppe.less'

export default class Gruppe extends Component {
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
			gruppe,
			createOrUpdateId,
			createGroup,
			fetching,
			testbrukere,
			testbrukerFetching,
			getGruppe
		} = this.props

		if (fetching) return <Loading label="laster gruppe" panel />

		if (!gruppe) return null

		return (
			<div id="gruppe-container">
				<Overskrift
					label={gruppe.navn}
					actions={[
						{
							icon: 'edit',
							onClick: createGroup
						},
						{
							icon: 'trashcan',
							onClick: () => {
								alert('ikke implementert')
							}
						}
					]}
				/>

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

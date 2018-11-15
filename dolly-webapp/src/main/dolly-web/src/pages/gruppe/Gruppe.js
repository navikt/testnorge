import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import GruppeDetaljer from './GruppeDetaljer/GruppeDetaljer'
import BestillingStatus from './BestillingStatus/BestillingStatus'
import Loading from '~/components/loading/Loading'
import TestbrukerListeConnector from './TestbrukerListe/TestbrukerListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import SendOpenAmConnector from './SendOpenAm/SendOpenAmConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import Toolbar from '~/components/toolbar/Toolbar'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'
import Knapp from 'nav-frontend-knapper'
import FavoriteButtonConnector from '~/components/button/FavoriteButton/FavoriteButtonConnector'

import './Gruppe.less'

export default class Gruppe extends Component {
	static propTypes = {
		gruppeArray: PropTypes.array,
		isFetching: PropTypes.bool,
		createOrUpdateId: PropTypes.string
	}

	VISNING_TESTPERSONER = 'testpersoner'
	VISNING_BESTILLING = 'bestilling'

	state = {
		redigerGruppe: false,
		visning: this.VISNING_TESTPERSONER
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
		this.setState({ visning }, () => this.props.resetSearch())
	}

	searchfieldPlaceholderSelector = () => {
		if (this.state.visning === this.VISNING_BESTILLING) return 'Søk i bestillinger'
		return 'Søk etter testpersoner'
	}

	renderList = gruppe => {
		const { visning } = this.state
		const { editTestbruker } = this.props

		if (visning === this.VISNING_BESTILLING)
			return <BestillingListeConnector bestillingListe={gruppe.bestillinger} />

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
			getBestillingStatus,
			bestillingStatus,
			addFavorite
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
		}

		const toggleValues = [
			{ value: this.VISNING_TESTPERSONER, label: `Testpersoner (${gruppe.testidenter.length})` },
			{ value: this.VISNING_BESTILLING, label: `Bestillinger (${gruppe.bestillinger.length})` }
		]
		return (
			<div id="gruppe-container">
				<Overskrift label={gruppe.navn} actions={groupActions}>
					{/* <ConfirmTooltip onClick={deleteGruppe} /> */}
					{!gruppe.erMedlemAvTeamSomEierGruppe && <FavoriteButtonConnector groupId={gruppe.id} />}
					<div className="pull-right">
						<SendOpenAmConnector gruppe={gruppe} />
					</div>
				</Overskrift>
				{createOrUpdateId && <RedigerGruppeConnector gruppe={gruppe} />}
				<GruppeDetaljer gruppe={gruppe} />

				{// Viser progressbar og bestillingsstatus
				gruppe.bestillinger.map(
					bestilling =>
						!bestilling.ferdig && (
							<BestillingStatus
								key={bestilling.id}
								bestilling={bestilling}
								onGroupUpdate={getGruppe}
								getBestillingStatus={getBestillingStatus}
								bestillingStatus={bestillingStatus}
							/>
						)
				)}

				<Toolbar
					searchField={<SearchFieldConnector placeholder={this.searchfieldPlaceholderSelector()} />}
					toggleOnChange={this.toggleToolbar}
					toggleCurrent={this.state.visning}
					toggleValues={toggleValues}
				>
					<Fragment>
						<Knapp type="hoved" onClick={this.startBestilling}>
							Opprett personer
						</Knapp>
					</Fragment>
				</Toolbar>
				{this.renderList(gruppe)}
			</div>
		)
	}
}

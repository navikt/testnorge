import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Knapp from 'nav-frontend-knapper'
import Overskrift from '~/components/overskrift/Overskrift'
import GruppeDetaljer from './GruppeDetaljer/GruppeDetaljer'
import StatusListeConnector from '~/components/bestilling/statusListe/StatusListeConnector'
import Loading from '~/components/loading/Loading'
import TestbrukerListeConnector from './TestbrukerListe/TestbrukerListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import Toolbar from '~/components/toolbar/Toolbar'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'
import FavoriteButtonConnector from '~/components/button/FavoriteButton/FavoriteButtonConnector'
import EksporterExcel from '~/pages/gruppe/EksporterExcel/EksporterExcel'
import './Gruppe.less'

export default class Gruppe extends Component {
	static propTypes = {
		gruppeArray: PropTypes.array,
		isFetching: PropTypes.bool,
		createOrUpdateId: PropTypes.number
	}

	VISNING_TESTPERSONER = 'testpersoner'
	VISNING_BESTILLING = 'bestilling'

	state = {
		redigerGruppe: false,
		visning: this.VISNING_TESTPERSONER
	}

	componentDidMount() {
		this.props.getGruppe()
		this.props.getBestillinger()
	}

	render() {
		const {
			gruppeArray,
			createOrUpdateId,
			createGroup,
			isFetching,
			deleteGruppe,
			antallBestillinger
		} = this.props

		if (isFetching && this.state.visning != this.VISNING_BESTILLING)
			return <Loading label="Laster testpersoner" panel />

		if (!gruppeArray) return null

		const gruppe = gruppeArray[0]

		let groupActions = []

		// Vise redigeringsknapp eller stjerne
		if (gruppe.erMedlemAvTeamSomEierGruppe) {
			groupActions.push({
				icon: 'edit',
				label: 'REDIGER',
				onClick: createGroup
			})
		}

		const toggleValues = [
			{
				value: this.VISNING_TESTPERSONER,
				label: `Testpersoner (${gruppe.testidenter ? gruppe.testidenter.length : 0})`
			},
			{
				value: this.VISNING_BESTILLING,
				label: `Bestillinger (${antallBestillinger})`
			}
		]

		return (
			<div className="gruppe-container">
				<div className="header-valg">
					<div>
						<Overskrift label={gruppe.navn} actions={groupActions}>
							{this.props.isDeletingGruppe ? (
								<Loading label="Sletter gruppe" panel />
							) : (
								<ConfirmTooltip
									label="SLETT"
									className="flexbox--align-center"
									message="Vil du slette denne testdatagruppen?"
									onClick={deleteGruppe}
								/>
							)}
							{!gruppe.erMedlemAvTeamSomEierGruppe && (
								<FavoriteButtonConnector groupId={gruppe.id} />
							)}
						</Overskrift>
					</div>
					<div className="hoyre">
						<EksporterExcel testidenter={gruppe.testidenter} gruppeId={gruppe.id} />
					</div>
				</div>
				{createOrUpdateId && <RedigerGruppeConnector gruppe={gruppe} />}
				<GruppeDetaljer gruppe={gruppe} />

				<StatusListeConnector gruppeId={gruppe.id} />

				<Toolbar
					searchField={<SearchFieldConnector placeholder={this.searchfieldPlaceholderSelector()} />}
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

	startBestilling = () => {
		const { gruppeId } = this.props.match.params
		this.props.history.push(`/gruppe/${gruppeId}/bestilling`)
	}

	toggleRedigerGruppe = () => this.setState({ redigerGruppe: !this.state.redigerGruppe })

	toggleToolbar = e => {
		const visning = e.target.value
		if (visning === this.VISNING_BESTILLING) this.props.getBestillinger()
		this.setState({ visning }, () => this.props.resetSearch())
	}

	searchfieldPlaceholderSelector = () => {
		if (this.state.visning === this.VISNING_BESTILLING) return 'Søk i bestillinger'
		return 'Søk etter testpersoner'
	}

	renderList = gruppe => {
		const { visning } = this.state
		const { editTestbruker } = this.props

		if (visning === this.VISNING_BESTILLING) return <BestillingListeConnector />

		return (
			<TestbrukerListeConnector testidenter={gruppe.testidenter} editTestbruker={editTestbruker} />
		)
	}
}

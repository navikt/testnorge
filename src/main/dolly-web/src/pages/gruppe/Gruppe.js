import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import GruppeDetaljer from './GruppeDetaljer/GruppeDetaljer'
import BestillingStatusConnector from './BestillingStatus/BestillingStatusConnector'
import Loading from '~/components/loading/Loading'
import TestbrukerListeConnector from './TestbrukerListe/TestbrukerListeConnector'
import BestillingListeConnector from './BestillingListe/BestillingListeConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import ConfirmTooltip from '~/components/confirmTooltip/ConfirmTooltip'
import Toolbar from '~/components/toolbar/Toolbar'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'
import Knapp from 'nav-frontend-knapper'
import FavoriteButtonConnector from '~/components/button/FavoriteButton/FavoriteButtonConnector'
import EksporterExcel from '~/pages/gruppe/EksporterExcel/EksporterExcel'
import _find from 'lodash/find'
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
		this.props.getBestillinger()
	}

	render() {
		const {
			gruppeArray,
			createOrUpdateId,
			createGroup,
			isFetching,
			isFetchingBestillinger,
			deleteGruppe,
			addFavorite,
			bestillinger,
			nyeBestillinger,
			getGruppe,
			getBestillinger
		} = this.props

		if (isFetching && this.state.visning != this.VISNING_BESTILLING)
			return <Loading label="Laster grupper" panel />

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
				label: `Bestillinger (${bestillinger.data ? bestillinger.data.length : 0})`
			}
		]

		return (
			<div className="gruppe-container">
				<Overskrift label={gruppe.navn} actions={groupActions}>
					<ConfirmTooltip
						label="SLETT"
						className="flexbox--align-center"
						message={'Vil du slette denne testdatagruppen?'}
						onClick={deleteGruppe}
					/>
					{!gruppe.erMedlemAvTeamSomEierGruppe && <FavoriteButtonConnector groupId={gruppe.id} />}
					{gruppeArray[0].testidenter &&
						<EksporterExcel 
							testidenter = {gruppeArray[0].testidenter} 
							gruppeId = {gruppeArray[0].id}
						/>
					}
				</Overskrift>
				{createOrUpdateId && <RedigerGruppeConnector gruppe={gruppe} />}
				<GruppeDetaljer gruppe={gruppe} />

				{// Viser progressbar og bestillingsstatus
				// Ikke render hvis det er ingen nye bestillinger besom tilhører gruppen
				!isFetchingBestillinger &&
					nyeBestillinger.map((bestilling, i) => {
						if (bestilling) {
							return (
								<BestillingStatusConnector
									key={i}
									bestilling={bestilling}
									onIdenterUpdate={getGruppe}
									onBestillingerUpdate={getBestillinger}
								/>
							)
						}
					})}

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

	startBestilling = () => {
		const { gruppeId } = this.props.match.params
		this.props.history.push(`/gruppe/${gruppeId}/bestilling`)
	}

	toggleRedigerGruppe = () => this.setState({ redigerGruppe: !this.state.redigerGruppe })

	toggleToolbar = e => {
		const visning = e.target.value
		visning === this.VISNING_BESTILLING && this.props.getBestillinger()
		this.setState({ visning }, () => this.props.resetSearch())
	}

	searchfieldPlaceholderSelector = () => {
		if (this.state.visning === this.VISNING_BESTILLING) return 'Søk i bestillinger'
		return 'Søk etter testpersoner'
	}

	renderList = gruppe => {
		const { visning } = this.state
		const { editTestbruker, bestillinger, isFetchingBestillinger } = this.props

		if (visning === this.VISNING_BESTILLING) {
			if (isFetchingBestillinger) {
				return <Loading label="Laster bestillinger" panel />
			}
			return <BestillingListeConnector bestillingListe={bestillinger} />
		}
		// !!! Pagination is is applied on TestbrukerListe because we fetch "testbrukere" from TPSF.
		// !!! Therefore pagination is applied to data from TPSF and not DOLLY.
		return (
			<TestbrukerListeConnector testidenter={gruppe.testidenter} editTestbruker={editTestbruker} />
		)
	}
}

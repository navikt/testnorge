import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import Toolbar from '~/components/toolbar/Toolbar'
import Knapp from 'nav-frontend-knapper'
import Liste from './Liste'
import Loading from '~/components/loading/Loading'
import ContentTooltip from '~/components/contentTooltip/ContentTooltip'
import './GruppeOversikt.less'

export default class GruppeOversikt extends PureComponent {
	static propTypes = {
		isFetching: PropTypes.bool,
		gruppeListe: PropTypes.array,
		visning: PropTypes.string,
		createOrUpdateId: PropTypes.number,
		history: PropTypes.object,
		listGrupper: PropTypes.func,
		settVisning: PropTypes.func,
		deleteGruppe: PropTypes.func
	}

	componentDidMount() {
		this.hentGrupper()
	}

	hentGrupper = () => this.props.listGrupper()
	byttVisning = e => {
		this.props.settVisning(e.target.value)
		this.props.listGrupper(e.target.value)
	}

	render() {
		const {
			isFetching,
			gruppeListe,
			visning,
			history,
			createOrUpdateId,
			editGroup,
			createGroup,
			deleteGruppe,
			setSort,
			sort
		} = this.props

		return (
			<div className="oversikt-container">
				<div className="page-header gruppe-oversikt-header">
					<Overskrift label="Testdatagrupper" />
					<ContentTooltip />
				</div>

				<Toolbar
					toggleOnChange={this.byttVisning}
					toggleCurrent={visning}
					searchField={SearchFieldConnector}
				>
					<Knapp type="hoved" onClick={createGroup}>
						Ny gruppe
					</Knapp>
				</Toolbar>

				{createOrUpdateId === -1 && <RedigerGruppeConnector />}

				{isFetching ? (
					<Loading label="laster grupper" panel />
				) : (
					<Liste
						items={gruppeListe}
						editId={createOrUpdateId}
						editGroup={editGroup}
						history={history}
						deleteGruppe={deleteGruppe}
						setSort={setSort}
						sort={sort}
					/>
				)}
			</div>
		)
	}
}

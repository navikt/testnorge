import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { ToggleGruppe, ToggleKnapp } from 'nav-frontend-skjema'
import Overskrift from '~/components/overskrift/Overskrift'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import Liste from './Liste'
import Loading from '~/components/loading/Loading'
import AddButton from '~/components/button/AddButton'
import ContentTooltip from '~/components/contentTooltip/ContentTooltip'

export default class GruppeOversikt extends PureComponent {
	static propTypes = {
		grupper: PropTypes.object,
		createOrUpdateId: PropTypes.number,
		history: PropTypes.object,
		getGrupper: PropTypes.func,
		settVisning: PropTypes.func,
		deleteGruppe: PropTypes.func
	}

	componentDidMount() {
		this.hentGrupper()
	}

	hentGrupper = () => this.props.getGrupper()
	byttVisning = e => {
		this.props.settVisning(e.target.value)
		this.props.getGrupper(e.target.value)
	}

	render() {
		const {
			gruppeListe,
			grupper,
			history,
			createOrUpdateId,
			editGroup,
			createGroup,
			deleteGruppe,
			setSort,
			sort,
			addFavorite
		} = this.props

		return (
			<div className="oversikt-container">
				<div className="page-header flexbox--space">
					<Overskrift label="Testdatagrupper">
						<ContentTooltip />
					</Overskrift>
				</div>

				<div className="flexbox--space">
					<ToggleGruppe onChange={this.byttVisning} name="toggleGruppe">
						<ToggleKnapp value="mine" checked={grupper.visning === 'mine'} key="1">
							Mine
						</ToggleKnapp>
						<ToggleKnapp value="alle" checked={grupper.visning === 'alle'} key="2">
							Alle
						</ToggleKnapp>
					</ToggleGruppe>
					<SearchFieldConnector />
				</div>

				{createOrUpdateId === -1 && <RedigerGruppeConnector />}

				{grupper.fetching ? (
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
						addFavorite={addFavorite}
					/>
				)}

				<AddButton title="Opprett ny gruppe" onClick={createGroup} />
			</div>
		)
	}
}

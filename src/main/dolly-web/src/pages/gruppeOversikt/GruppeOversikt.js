import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Knapp from 'nav-frontend-knapper'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import Toolbar from '~/components/ui/toolbar/Toolbar'
import Liste from './Liste'

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
			createGroup,
			searchActive
		} = this.props

		return (
			<div className="oversikt-container">
				<div className="page-header flexbox--align-center--justify-start">
					<Overskrift label="Testdatagrupper" />
					<HjelpeTekst>
						Testdatagruppen inneholder alle testpersonene dine (FNR/DNR/BOST).
					</HjelpeTekst>
				</div>

				<Toolbar
					toggleOnChange={this.byttVisning}
					toggleCurrent={visning}
					searchField={<SearchFieldConnector />}
				>
					<Knapp type="hoved" onClick={createGroup}>
						Ny gruppe
					</Knapp>
				</Toolbar>

				{createOrUpdateId === -1 && <RedigerGruppeConnector />}

				<Liste
					items={gruppeListe}
					history={history}
					isFetching={isFetching}
					searchActive={searchActive}
				/>
			</div>
		)
	}
}

import React, { PureComponent } from 'react'
import Knapp from 'nav-frontend-knapper'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import SearchFieldConnector from '~/components/searchField/SearchFieldConnector'
import RedigerGruppeConnector from '~/components/redigerGruppe/RedigerGruppeConnector'
import Toolbar from '~/components/ui/toolbar/Toolbar'
import Liste from './Liste'

export default class GruppeOversikt extends PureComponent {
	componentDidMount() {
		this.hentGrupper()
	}

	state = {
		visNyGruppe: false
	}

	hentGrupper = () => this.props.listGrupper()
	byttVisning = e => {
		this.props.settVisning(e.target.value)
		this.props.listGrupper(e.target.value)
	}

	visNyGruppe = () => this.setState({ visNyGruppe: true })
	skjulNyGruppe = () => this.setState({ visNyGruppe: false })

	render() {
		const { isFetching, gruppeListe, visning, history, searchActive } = this.props

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
					<Knapp type="hoved" onClick={this.visNyGruppe}>
						Ny gruppe
					</Knapp>
				</Toolbar>

				{this.state.visNyGruppe && <RedigerGruppeConnector onCancel={this.skjulNyGruppe} />}

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

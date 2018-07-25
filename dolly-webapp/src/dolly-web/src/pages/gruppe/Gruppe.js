import React, { Component } from 'react'
import Knapp from 'nav-frontend-knapper'
import Icon from '~/components/icon/Icon'
import Overskrift from '~/components/overskrift/Overskrift'
import GruppeDetaljer from './GruppeDetaljer'
import Loading from '~/components/loading/Loading'
import Table from '~/components/table/Table'
import PersonDetaljer from './PersonDetaljer'

import './Gruppe.less'

export default class Gruppe extends Component {
	componentDidMount() {
		// TODO: Currently handles refresh on pageload
		if (!this.props.gruppe) {
			const groupId = this.props.match.params.gruppeId
			this.props.getGrupper().then(res => {
				const currentGroupObject = res.grupper.find(gruppe => gruppe.id === parseInt(groupId))
				//TEMP - skal få ut i plain array
				const brukerListe = currentGroupObject.testidenter.map(ident => ident.ident)
				if (brukerListe.length === 0) return false
				this.props.getTpsfBruker(brukerListe)
			})
		}
	}

	startOppskrift = () => {
		const { gruppeId } = this.props.match.params
		this.props.history.push(`/gruppe/${gruppeId}/oppskrift`)
	}

	render() {
		const { gruppe, fetching, testbrukere } = this.props

		if (fetching) return <Loading label="laster gruppe" panel />

		if (!gruppe) return false

		return (
			<div id="gruppe-container">
				<Overskrift
					label={gruppe.navn}
					actions={[{ icon: 'edit', onClick: () => {} }, { icon: 'trashcan', onClick: () => {} }]}
				/>

				<GruppeDetaljer gruppe={gruppe} />

				<Overskrift
					type="h2"
					label="Testpersoner"
					actions={[{ icon: 'add-circle', onClick: this.startOppskrift }]}
				/>

				<Table>
					<Table.Header>
						<Table.Column width="15" value="ID" />
						<Table.Column width="15" value="ID-type" />
						<Table.Column width="30" value="Navn" />
						<Table.Column width="10" value="Alder" />
						<Table.Column width="20" value="Kjønn" />
					</Table.Header>

					{testbrukere &&
						testbrukere.map((bruker, idx) => {
							return (
								<Table.Row key={idx} expandComponent={<PersonDetaljer />}>
									<Table.Column width="15" value={bruker.ident} />
									<Table.Column width="15" value={bruker.identtype} />
									<Table.Column width="30" value={`${bruker.etternavn}, ${bruker.fornavn}`} />
									<Table.Column width="10" value={bruker.alder || 'Ikke definert'} />
									<Table.Column width="20" value={bruker.kjonn} />
								</Table.Row>
							)
						})}
				</Table>
			</div>
		)
	}
}

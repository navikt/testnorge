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
		if (!this.props.gruppe) this.props.getGrupper()
	}

	startOppskrift = () => {
		const { gruppeId } = this.props.match.params
		this.props.history.push(`/gruppe/${gruppeId}/oppskrift`)
	}

	render() {
		const { gruppe, fetching } = this.props

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

					{tempGRUPPE.map((o, idx) => {
						return (
							<Table.Row key={idx} expandComponent={<PersonDetaljer />}>
								<Table.Column width="15" value={o.id} />
								<Table.Column width="15" value={o.idType} />
								<Table.Column width="30" value={o.navn} />
								<Table.Column width="10" value={o.alder} />
								<Table.Column width="20" value={o.kjonn} />
							</Table.Row>
						)
					})}
				</Table>
			</div>
		)
	}
}

const tempGRUPPE = [
	{
		id: '010887 39501',
		idType: 'FNR',
		navn: 'Lunder, Helga Woll',
		kjonn: 'Mann',
		alder: '30',
		gt: 'Drøbak',
		arbforhold: 'ordinært'
	},
	{
		id: '010887 39502',
		idType: 'FNR',
		navn: 'Lunder, Helga Woll',
		kjonn: 'Mann',
		alder: '30',
		gt: 'Drøbak',
		arbforhold: 'ordinært'
	},
	{
		id: '010887 39503',
		idType: 'FNR',
		navn: 'Lunder, Helga Woll',
		kjonn: 'Mann',
		alder: '30',
		gt: 'Drøbak',
		arbforhold: 'ordinært'
	},
	{
		id: '010887 39504',
		idType: 'FNR',
		navn: 'Lunder, Helga Woll',
		kjonn: 'Mann',
		alder: '30',
		gt: 'Drøbak',
		arbforhold: 'ordinært'
	}
]

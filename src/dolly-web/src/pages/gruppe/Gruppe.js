import React, { Component } from 'react'
import Knapp from 'nav-frontend-knapper'
import Icon from '~/components/icon/Icon'
import Overskrift from '~/components/overskrift/Overskrift'
import GruppeDetaljer from './GruppeDetaljer'
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
		const { gruppe } = this.props
		if (!gruppe) return false

		return (
			<div id="gruppe-container">
				<Overskrift
					label={gruppe.navn}
					actions={[{ icon: 'pencil', onClick: () => {} }, { icon: 'trash-o', onClick: () => {} }]}
				/>

				<div style={{ padding: '30px' }}>
					<Icon kind="trashcan" />
					<Icon kind="add-circle" />
					<Icon kind="edit" />
					<Icon kind="star" />
					<Icon kind="star-filled" />
					<Icon kind="user" />
					<Icon kind="remove-circle" />
					<Icon kind="search" />
					<Icon kind="chevron-down" />
					<Icon kind="chevron-up" />
					<Icon kind="chevron-left" />
					<Icon kind="chevron-right" />
				</div>

				<GruppeDetaljer data={tempGRUPPEHEADER} />

				<Overskrift
					type="h2"
					label="Testpersoner"
					actions={[{ icon: 'plus-circle', onClick: this.startOppskrift }]}
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
							<Table.Row key={idx} expandComponent={<PersonDetaljer />} actionWidth="10">
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

const tempGRUPPEHEADER = {
	eier: 'Helga Woll Lunder',
	team: 'Foreldrepenger',
	env: 'T5, T6 og T7',
	personer_num: '30',
	menn_num: '23',
	kvinner_num: '7',
	opprettet: '10.06.2018',
	sistEndret: '15.06.2018',
	hensikt: 'Officia non nisi amet sit est proident culpa ipsum commodo consectetur minim officia.'
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

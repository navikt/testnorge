import React, { Component } from 'react'
import Knapp from 'nav-frontend-knapper'
import Ikon from 'nav-frontend-ikoner-assets'
import GruppeDetaljer from './GruppeDetaljer'
import Table from '~/components/table/Table'

import './Gruppe.less'

export default class Gruppe extends Component {
	componentDidMount() {
		// TODO: Currently handles refresh on pageload
		if (!this.props.gruppe) this.props.getGrupper()
	}

	render() {
		const { gruppe } = this.props
		if (!gruppe) return false

		return (
			<div id="gruppe-container">
				<div className="content-header">
					<h1>
						{gruppe.navn} <i className="fa fa-pencil" /> <Ikon kind="trashcan" />
					</h1>
					<div className="content-header-buttons">
						<Knapp type="standard">Legg til personer</Knapp>
						<Knapp type="standard">Dupliser gruppe</Knapp>
					</div>
				</div>

				<GruppeDetaljer data={tempGRUPPEHEADER} />

				{tempGRUPPE && <Table data={tempGRUPPE} selectable expandable />}
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

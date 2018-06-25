import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Panel from '~/components/panel/Panel'
import Input from '~/components/fields/Input/Input'
import Utvalg from './Utvalg/Utvalg'
import Checkbox from '~/components/fields/Checkbox/Checkbox'

import './AttributtVelger.less'

export default class AttributtVelger extends Component {
	static propTypes = {
		onChange: PropTypes.func.isRequired
	}

	state = {
		search: '',
		selection: {}
	}

	searchOnChange = e => this.setState({ search: e.target.value })

	render() {
		return (
			<div className="attributt-velger">
				<Input
					label="Søk attributter"
					labelOffscreen
					placeholder="Søk attributter"
					className="attributt-velger_search"
					onChange={this.searchOnChange}
				/>

				<div className="flexbox">
					<div className="attributt-velger_panels">
						<Panel heading={<h3>Personinformasjon</h3>}>
							<div className="attributt-velger_panelcontent">
								<Checkbox label="Fornavn" id="fornavn" />
								<Checkbox label="Mellomnavn" id="mellomnavn" />
								<Checkbox label="Etternavn" id="etternavn" />
								<Checkbox label="Kjønn" id="kjonn" />
								<Checkbox label="Statsborgerskap" id="statsborgerskap" />
								<Checkbox label="Født før" id="fodtFor" />
								<Checkbox label="Født etter" id="fodtEtter" />
								<Checkbox label="Spesialregister" id="spesialRegister" />
								<Checkbox label="Spes.reg dato" id="spesregdato" />
								<Checkbox label="Dødsdato" id="dodsdato" />
							</div>
						</Panel>

						<Panel heading={<h3>Adresser</h3>}>
							<div className="attributt-velger_panelcontent">
								<Checkbox label="Fornavn" id="fornavn1" />
								<Checkbox label="Mellomnavn" id="mellomnavn1" />
								<Checkbox label="Etternavn" id="etternavn1" />
								<Checkbox label="Kjønn" id="kjonn1" />
								<Checkbox label="Statsborgerskap" id="statsborgerskap1" />
								<Checkbox label="Født før" id="fodtFor1" />
								<Checkbox label="Født etter" id="fodtEtter1" />
								<Checkbox label="Spesialregister" id="spesialRegister1" />
								<Checkbox label="Spes.reg dato" id="spesregdato1" />
								<Checkbox label="Dødsdato" id="dodsdato1" />
							</div>
						</Panel>

						<Panel heading={<h3>Familierelasjoner</h3>}>
							<div className="attributt-velger_panelcontent">
								<Checkbox label="Fornavn" id="fornavn2" />
								<Checkbox label="Mellomnavn" id="mellomnavn2" />
								<Checkbox label="Etternavn" id="etternavn2" />
								<Checkbox label="Kjønn" id="kjonn2" />
								<Checkbox label="Statsborgerskap" id="statsborgerskap2" />
								<Checkbox label="Født før" id="fodtFor2" />
								<Checkbox label="Født etter" id="fodtEtter2" />
								<Checkbox label="Spesialregister" id="spesialRegister2" />
								<Checkbox label="Spes.reg dato" id="spesregdato2" />
								<Checkbox label="Dødsdato" id="dodsdato2" />
							</div>
						</Panel>
					</div>

					<Utvalg />
				</div>
			</div>
		)
	}
}

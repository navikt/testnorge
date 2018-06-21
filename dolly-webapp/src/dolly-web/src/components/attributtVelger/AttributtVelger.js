import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Panel from '~/components/panel/Panel'
import Input from '~/components/fields/Input/Input'
import Utvalg from './Utvalg/Utvalg'

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

				<div className="row">
					<div className="attributt-velger_panels">
						<Panel
							heading={<h3>Personinformasjon</h3>}
							content={'Ex nostrud incididunt proident sunt irure excepteur.'}
						/>

						<Panel
							heading={<h3>Adresser</h3>}
							content={'Ex nostrud incididunt proident sunt irure excepteur.'}
						/>

						<Panel
							heading={<h3>Familierelasjoner</h3>}
							content={'Ex nostrud incididunt proident sunt irure excepteur.'}
						/>
					</div>

					<Utvalg />
				</div>
			</div>
		)
	}
}

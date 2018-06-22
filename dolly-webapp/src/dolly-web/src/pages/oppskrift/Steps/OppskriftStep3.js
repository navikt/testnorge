import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Knapp from 'nav-frontend-knapper'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Overskrift from '~/components/overskrift/Overskrift'

export default class OppskriftStep3 extends Component {
	static propTypes = {}

	render() {
		return (
			<div>
				<div className="content-header">
					<Overskrift label="Oppsummering" />
				</div>

				<div className="oppsummering">
					<div className="oppsummering-values">
						<StaticValue header="Type" value="FNR" />
						<StaticValue header="Antall personer" value="16" />
						<StaticValue header="Født før" value="01.01.2018" />
						<StaticValue header="Født etter" value="20.06.2007" />
						<StaticValue header="Kjønn" value="Uspesifisert" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
					</div>
					<h4>Defaultverdier som blir satt</h4>
					<div className="oppsummering-values">
						<StaticValue header="Statsborgerskap" value="Norsk" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
						<StaticValue header="Label" value="Noe informasjon" />
					</div>
				</div>

				<Knapp type="hoved">Opprett testpersoner</Knapp>
			</div>
		)
	}
}

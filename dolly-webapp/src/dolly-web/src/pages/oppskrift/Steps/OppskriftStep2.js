import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Input from '~/components/fields/Input/Input'
import { Select } from 'nav-frontend-skjema'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Overskrift from '~/components/overskrift/Overskrift'
import Panel from '~/components/panel/Panel'

export default class OppskriftStep2 extends Component {
	static propTypes = {}

	render() {
		return (
			<div>
				<div className="content-header">
					<Overskrift label="Velg verdier" />
				</div>

				<div className="grunnoppsett">
					<StaticValue header="TYPE" value="FNR" />
					<StaticValue header="ANTALL PERSONER" value="15" />
				</div>

				<Panel heading={<h3>Personinformasjon</h3>}>
					Ullamco Lorem non voluptate adipisicing ipsum pariatur fugiat sint eu nostrud commodo.
				</Panel>

				<Panel heading={<h3>Adresser</h3>}>
					Ullamco Lorem non voluptate adipisicing ipsum pariatur fugiat sint eu nostrud commodo.
				</Panel>

				<Panel heading={<h3>Arbeidsforhold</h3>}>
					Ullamco Lorem non voluptate adipisicing ipsum pariatur fugiat sint eu nostrud commodo.
				</Panel>
			</div>
		)
	}
}

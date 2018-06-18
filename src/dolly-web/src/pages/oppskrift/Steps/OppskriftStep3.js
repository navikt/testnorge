import React, { Component } from 'react'
import Input from '~/components/fields/Input/Input'
import { Select } from 'nav-frontend-skjema'
import PropTypes from 'prop-types'
import Knapp from 'nav-frontend-knapper'

export default class OppskriftStep3 extends Component {
	static propTypes = {}

	render() {
		return (
			<div>
				<div className="content-header">
					<h1>Oppsummering</h1>
				</div>

				<Knapp type="hoved">Opprett testpersoner</Knapp>
			</div>
		)
	}
}

import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Input from '~/components/fields/Input/Input'
import Label from '~/components/fields/Label/Label'
import { Select } from 'nav-frontend-skjema'

export default class OppskriftStep1 extends Component {
	static propTypes = {}

	render() {
		return (
			<div>
				<div className="content-header">
					<h1>Legg til testpersoner</h1>
				</div>

				<Select label={<Label label="Type" />}>
					<option value="fnr" key="fnr">
						FNR
					</option>
					<option value="dnr" key="dnr">
						DNR
					</option>
				</Select>

				<Input label={<Label label="Antall personer" />} />
			</div>
		)
	}
}

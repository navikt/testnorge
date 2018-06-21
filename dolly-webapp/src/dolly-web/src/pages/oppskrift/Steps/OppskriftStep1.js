import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Input from '~/components/fields/Input/Input'
import Label from '~/components/fields/Label/Label'
import { Select } from 'nav-frontend-skjema'
import AttributtVelger from '~/components/attributtVelger/AttributtVelger'
import Overskrift from '~/components/overskrift/Overskrift'

export default class OppskriftStep1 extends Component {
	static propTypes = {}

	render() {
		return (
			<div>
				<div className="content-header">
					<Overskrift label="Legg til testpersoner" />
				</div>

				<div className="row">
					<Select label={<Label label="Type" />} className="input-fnr">
						<option value="fnr" key="fnr">
							FNR
						</option>
						<option value="dnr" key="dnr">
							DNR
						</option>
					</Select>

					<Input label={<Label label="Antall personer" />} className="input-num-person" />
				</div>

				<AttributtVelger onChange={() => {}} />
			</div>
		)
	}
}

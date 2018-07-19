import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Input from '~/components/fields/Input/Input'
import Label from '~/components/fields/Label/Label'
import { Select } from 'nav-frontend-skjema'
import AttributtVelger from '~/components/attributtVelger/AttributtVelger'
import Overskrift from '~/components/overskrift/Overskrift'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { Form, Field } from 'formik'

export default class OppskriftStep1 extends Component {
	static propTypes = {}

	render() {
		const options = [{ value: 'FNR', label: 'FNR' }, { value: 'DNR', label: 'DNR' }]

		return (
			<div>
				<div className="flexbox--space">
					<Overskrift label="Legg til testpersoner" />
				</div>

				<div className="flexbox">
					<Select label={<Label label="Type" />} className="input-fnr">
						<option value="fnr" key="fnr">
							FNR
						</option>
						<option value="dnr" key="dnr">
							DNR
						</option>
					</Select>
					<Field
						name="antall"
						label="Antall personer"
						className="input-num-person"
						component={FormikInput}
					/>
				</div>

				<AttributtVelger onChange={() => {}} />
			</div>
		)
	}
}

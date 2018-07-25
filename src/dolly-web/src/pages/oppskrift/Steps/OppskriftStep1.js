import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Input from '~/components/fields/Input/Input'
import Label from '~/components/fields/Label/Label'
import { Select } from 'nav-frontend-skjema'
import AttributtVelger from '~/components/attributtVelger/AttributtVelger'
import Overskrift from '~/components/overskrift/Overskrift'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { Field } from 'formik'

export default class OppskriftStep1 extends Component {
	static propTypes = {}

	render() {
		// ident typer
		const options = [{ value: 'FNR', label: 'FNR' }, { value: 'DNR', label: 'DNR' }]

		return (
			<div>
				<div className="flexbox--space">
					<Overskrift label="Legg til testpersoner" />
				</div>

				<div className="flexbox">
					<Field
						name="identtype"
						label="Velg identtype"
						component={FormikDollySelect}
						options={options}
					/>
					<Field
						name="antall"
						label="Antall personer"
						className="input-num-person"
						type="number"
						component={FormikInput}
					/>
				</div>

				<AttributtVelger
					onChange={() => {}}
					attributter={this.props.attributter}
					selectedTypes={this.props.selectedTypes}
					onSelectionHandler={this.props.onSelectionHandler}
				/>
			</div>
		)
	}
}

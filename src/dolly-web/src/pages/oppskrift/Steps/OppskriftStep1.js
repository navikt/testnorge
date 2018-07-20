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
		// identtype
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
						component={FormikInput}
					/>
				</div>

				<AttributtVelger onChange={() => {}} />
			</div>
		)
	}
}

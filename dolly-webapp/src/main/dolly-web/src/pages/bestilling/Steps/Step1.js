import React, { Component } from 'react'
import PropTypes from 'prop-types'
import AttributtVelger from '~/components/attributtVelger/AttributtVelger'
import Overskrift from '~/components/overskrift/Overskrift'
import * as yup from 'yup'
import NavigationConnector from '../Navigation/NavigationConnector'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { Field, withFormik } from 'formik'
import { animateScroll } from 'react-scroll'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'

class Step1 extends Component {
	static propTypes = {
		identtype: PropTypes.string,
		antall: PropTypes.number,
		selectedAttributeIds: PropTypes.array,
		startBestilling: PropTypes.func,
		toggleAttributeSelection: PropTypes.func
	}

	_onSubmitForm = () => {
		this.props.submitForm()
		animateScroll.scrollToTop({ duration: 250 })
	}
	render() {
		const { selectedAttributeIds, toggleAttributeSelection, uncheckAllAttributes } = this.props

		return (
			<div className="bestilling-step1">
				<div className="flexbox--space">
					<Overskrift label="Velg egenskaper" />
				</div>

				<div className="flexbox">
					<Field
						name="identtype"
						label="Velg identtype"
						component={FormikDollySelect}
						options={SelectOptionsManager('identtype')}
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
					onToggle={toggleAttributeSelection}
					uncheckAllAttributes={uncheckAllAttributes}
					selectedIds={selectedAttributeIds}
				/>

				<NavigationConnector onClickNext={this._onSubmitForm} />
			</div>
		)
	}
}

export default withFormik({
	displayName: 'BestillingStep1',
	mapPropsToValues: props => ({
		identtype: props.identtype || 'FNR', // default to FNR
		antall: props.antall
	}),
	validationSchema: yup.object().shape({
		antall: yup
			.number()
			.min(1, 'Må minst opprette 1 testperson')
			.max(5, 'Maks 5 personer i første omgang')
			.required('Oppgi antall testbrukere'),
		identtype: yup.string().required('Velg en identtype')
	}),
	handleSubmit: (values, { props, setSubmitting, setErrors }) => {
		props.startBestilling(values)
	}
})(Step1)

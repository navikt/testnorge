import React, { Component } from 'react'
import PropTypes from 'prop-types'
import StepIndicator from './Steps/StepIndicator'
import OppskriftSteg1 from './Steps/OppskriftStep1'
import OppskriftSteg2 from './Steps/OppskriftStep2'
import OppskriftSteg3 from './Steps/OppskriftStep3'
import { Formik, Form } from 'formik'
import * as yup from 'yup'
import FormErrors from '~/components/formErrors/FormErrors'
import DisplayFormikState from '~/utils/DisplayFormikState'

import './Oppskrift.less'

export default class Oppskrift extends Component {
	static propTypes = {}

	state = {
		activeStep: 0,
		selection: {}
	}

	onHandleSubmit = (values, actions) => {
		console.log(values)
	}

	onSelectionHandler = e => {}

	validation = () =>
		yup.object().shape({
			antall: yup.number().required('Oppgi antall testbrukere')
		})

	render() {
		const { activeStep } = this.state

		let initialValues = {
			identtype: 'FNR',
			kjonn: 'M',
			foedtEtter: '2015-01-31T00:00:00.000Z',
			foedtFoer: '2018-07-16T00:00:00.000Z',
			regdato: '2018-07-16T00:00:00.000Z',
			withAdresse: false,
			statsborgerskap: 'NOR',
			antall: 2,

			environments: ['u6']
		}

		return (
			<Formik
				initialValues={initialValues}
				validationSchema={this.validation}
				onSubmit={this.onHandleSubmit}
				render={props => {
					const { values, touched, errors, dirty, isSubmitting } = props
					return (
						<div className="oppskrift-page">
							<StepIndicator activeStep={activeStep} />
							<Form autoComplete="off">
								{activeStep === 0 && <OppskriftSteg1 />}
								{activeStep === 1 && <OppskriftSteg2 />}
								{activeStep === 2 && <OppskriftSteg3 />}
							</Form>

							<button onClick={() => {}}>Avbryt</button>

							{activeStep !== 0 && (
								<button onClick={() => this.setState({ activeStep: activeStep - 1 })}>
									Tilbake
								</button>
							)}
							{activeStep !== 2 && (
								<button onClick={() => this.setState({ activeStep: activeStep + 1 })}>
									Videre
								</button>
							)}
							<FormErrors errors={errors} touched={touched} />
							<DisplayFormikState {...props} />
						</div>
					)
				}}
			/>
		)
	}
}

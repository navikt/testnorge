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
import Attributter from './Attributter'
import { DollyApi } from '~/service/Api'

import './Oppskrift.less'

export default class Oppskrift extends Component {
	static propTypes = {}

	state = {
		activeStep: 0,
		selection: {},
		selectedTypes: {
			foedtEtter: false,
			foedtFoer: false,
			kjonn: false,
			regdato: false,
			statsborgerskap: false,
			withAdresse: false
		}
	}

	onHandleSubmit = async (values, actions) => {
		const gruppeId = this.props.match.params.gruppeId
		try {
			const res = await DollyApi.createBestilling(gruppeId, values)

			this.props.history.push(`/gruppe/${gruppeId}`)
		} catch (error) {
			console.log('error', error)
		}
	}

	onSelectionHandler = e => {
		const selectionId = e.target.id
		const value = e.target.checked
		this.setState(prevState => {
			return { selectedTypes: { ...prevState.selectedTypes, [selectionId]: value } }
		})
	}

	validation = () =>
		yup.object().shape({
			antall: yup.number().required('Oppgi antall testbrukere')
		})

	render() {
		const { activeStep, selectedTypes } = this.state

		let initialValues = {
			identtype: null, //string
			kjonn: null, // string
			foedtEtter: null, // string
			foedtFoer: null, // string
			regdato: new Date(), // string
			withAdresse: true, // bool
			statsborgerskap: null, // string
			antall: null, // number

			environments: [] // array
		}

		// test data
		// let initialValues = {
		// 	identtype: 'FNR',
		// 	kjonn: 'M',
		// 	foedtEtter: '2018-07-14T10:00:00.000Z',
		// 	foedtFoer: '2018-07-02T10:00:00.000Z',
		// 	regdato: '2018-07-24T11:59:49.051Z',
		// 	withAdresse: true,
		// 	statsborgerskap: 'NOR',
		// 	antall: 3,
		// 	environments: ['u6', 't1', 't2']
		// }

		const stegProps = {
			attributter: Attributter,
			selectedTypes
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
								{activeStep === 0 && (
									<OppskriftSteg1 onSelectionHandler={this.onSelectionHandler} {...stegProps} />
								)}
								{activeStep === 1 && <OppskriftSteg2 {...stegProps} values={values} />}
								{activeStep === 2 && <OppskriftSteg3 {...stegProps} values={values} />}
							</Form>

							<button onClick={() => this.props.history.goBack()}>Avbryt</button>

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

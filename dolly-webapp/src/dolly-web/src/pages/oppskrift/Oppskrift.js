import React, { Component } from 'react'
import PropTypes from 'prop-types'
import OppskriftSteg1 from './Steps/OppskriftStep1'
import OppskriftSteg2 from './Steps/OppskriftStep2'
import OppskriftSteg3 from './Steps/OppskriftStep3'
import { Formik, Form } from 'formik'
import * as yup from 'yup'
import Attributter from './Attributter'
import { DollyApi } from '~/service/Api'
import Wizard from '~/components/wizard/Wizard'
import AttributtKodeverk from './AttributtKodeverk.ts'

import './Oppskrift.less'

const validationList = [
	yup.object().shape({
		antall: yup
			.number()
			.max(5, 'Maks 5 personer i første omgang')
			.required('Oppgi antall testbrukere'),
		identtype: yup.string().required('Velg en identtype')
	}),
	yup.object().shape({
		kjonn: yup.string().required('Velg kjønn'),
		foedtEtter: yup.string().required('Velg en dato'),
		foedtFoer: yup.string().required('Velg en dato'),
		statsborgerskap: yup.string().required('Krever et statsborgerskap')
		// regdato: '2018-07-24T11:59:49.051Z',
		// withAdresse: true,
		// environments: ['u6', 't1', 't2']
	}),
	yup.object().shape({
		environments: yup.array().required('Velg minst ett miljø')
	})
]

export default class Oppskrift extends Component {
	static propTypes = {}

	state = {
		selectedTypes: {
			foedtEtter: true,
			foedtFoer: true,
			kjonn: true,
			regdato: false,
			statsborgerskap: true,
			withAdresse: false
		},
		selectedAttributes: []
	}

	onHandleSubmit = async (values, actions) => {
		const { gruppeId } = this.props.match.params
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

	onCancelHandler = () => {
		this.props.history.goBack()
	}

	render() {
		const { activeStep, selectedTypes } = this.state

		let initialValues = {
			identtype: '', //string
			kjonn: '', // string
			foedtEtter: '', // string
			foedtFoer: '', // string
			regdato: new Date(), // string
			withAdresse: true, // bool
			statsborgerskap: '', // string
			antall: '', // number

			environments: [] // array
		}

		const stegProps = {
			attributter: Attributter,
			selectedTypes
		}

		return (
			<Wizard
				initialValues={initialValues}
				onSubmit={this.onHandleSubmit}
				validationSchemaList={validationList}
				onCancelHandler={this.onCancelHandler}
			>
				<OppskriftSteg1 {...stegProps} onSelectionHandler={this.onSelectionHandler} />
				<OppskriftSteg2 {...stegProps} />
				<OppskriftSteg3 {...stegProps} />
			</Wizard>
		)
	}
}

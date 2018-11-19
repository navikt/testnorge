import React, { Component } from 'react'
import _merge from 'lodash/merge'
import Knapp from 'nav-frontend-knapper'
import { AttributtManager } from '~/service/Kodeverk'
import { Formik } from 'formik'
import FormEditor from '~/components/formEditor/FormEditor'
import DisplayFormikState from '~/utils/DisplayFormikState'
import Button from '~/components/button/Button'
import _set from 'lodash/set'
import DataFormatter from '~/utils/DataFormatter'

import './RedigerTestbruker.less'

export default class RedigerTestbruker extends Component {
	constructor() {
		super()
		this.AttributtManager = new AttributtManager()
		this.AttributtListe = this.AttributtManager.listEditable()
		this.AttributtListeFlat = this.AttributtManager.listEditableFlat()
		this.Validations = this.AttributtManager.getValidationsForEdit()
	}

	componentDidMount() {
		this.props.getGruppe()
		this.props.getTestbruker()
		this.props.getSigrunTestbruker()
	}

	submit = values => {
		const { testbruker, testbrukerEnvironments, updateTestbruker, goBack } = this.props

		const valuesMapped = this.AttributtListeFlat.reduce((prev, curr) => {
			let currentValue = values[curr.id]
			if (curr.inputType === 'date') currentValue = DataFormatter.parseDate(currentValue)
			return _set(prev, curr.path || curr.id, currentValue)
		}, {})

		const tpsData = {
			identer: [testbruker.ident],
			miljoer: testbrukerEnvironments
		}

		updateTestbruker(_merge(testbruker, valuesMapped), tpsData)
		goBack()
	}

	render() {
		const { testbruker, goBack } = this.props

		if (!testbruker) return null

		const initialValues = this.AttributtManager.getInitialValuesForEditableItems(testbruker)

		return (
			<Formik
				onSubmit={this.submit}
				initialValues={initialValues}
				validationSchema={this.Validations}
				render={formikProps => (
					<div>
						<h2>Rediger {`${testbruker.fornavn} ${testbruker.etternavn}`}</h2>
						<FormEditor
							AttributtListe={this.AttributtListe}
							FormikProps={formikProps}
							ClosePanels
						/>
						<div className="form-editor-knapper">
							<Knapp type="standard" onClick={goBack}>
								Avbryt
							</Knapp>
							<Knapp type="hoved" onClick={formikProps.submitForm}>
								Lagre
							</Knapp>
						</div>
						<DisplayFormikState {...formikProps} />
					</div>
				)}
			/>
		)
	}
}

import React, { Component } from 'react'
import _merge from 'lodash/merge'
import Knapp from 'nav-frontend-knapper'
import { AttributtManager } from '~/service/Kodeverk'
import { Formik } from 'formik'
import FormEditor from '~/components/formEditor/FormEditor'
import DisplayFormikState from '~/utils/DisplayFormikState'
import Button from '~/components/button/Button'

import './RedigerTestbruker.less'

export default class RedigerTestbruker extends Component {
	constructor() {
		super()
		this.AttributtManager = new AttributtManager()
		this.AttributtListe = this.AttributtManager.listEditable()
	}

	componentDidMount() {
		this.props.getTestbruker()
	}

	submit = values => {
		const { testbruker, updateTestbruker, goBack } = this.props

		updateTestbruker(_merge(testbruker, values))
		goBack()
	}

	render() {
		const { testbruker, goBack } = this.props

		if (!testbruker) return null

		const initialValues = this.AttributtManager.getInitialValuesForEditableItems(testbruker)

		// console.log('attributtliste', this.AttributtListe)
		// console.log('iinit vals', initialValues)

		return (
			<Formik
				onSubmit={this.submit}
				initialValues={initialValues}
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
						{/* <DisplayFormikState {...formikProps} /> */}
					</div>
				)}
			/>
		)
	}
}

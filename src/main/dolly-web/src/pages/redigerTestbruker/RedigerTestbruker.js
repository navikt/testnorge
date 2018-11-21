import React, { Component } from 'react'
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
		this.AttributtListeFlat = this.AttributtManager.listEditableFlat()
		this.Validations = this.AttributtManager.getValidationsForEdit()
	}

	componentDidMount() {
		this.props.getGruppe()
		this.props.getTestbruker()
		this.props.getSigrunTestbruker()
		this.props.getKrrTestbruker()
	}

	submit = values => {
		const { updateTestbruker, goBack } = this.props

		updateTestbruker(values, this.AttributtListeFlat)
		goBack()
	}

	render() {
		const { testbruker, goBack, match } = this.props
		const { tpsf, sigrunstub, krrstub } = testbruker

		if (!tpsf || !sigrunstub || !krrstub) return null

		const initialValues = this.AttributtManager.getInitialValuesForEditableItems(
			testbruker,
			match.params.ident
		)

		return (
			<Formik
				onSubmit={this.submit}
				initialValues={initialValues}
				validationSchema={this.Validations}
				render={formikProps => (
					<div>
						<h2>Rediger {`${tpsf[0].fornavn} ${tpsf[0].etternavn}`}</h2>
						<FormEditor
							AttributtListe={this.AttributtListe}
							FormikProps={formikProps}
							ClosePanels
							editMode
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

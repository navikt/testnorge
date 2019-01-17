import React, { Component } from 'react'
import Knapp from 'nav-frontend-knapper'
import { AttributtManager } from '~/service/Kodeverk'
import { DataSource } from '~/service/kodeverk/AttributtManager/Types'
import { Formik } from 'formik'
import FormEditor from '~/components/formEditor/FormEditor'
import DisplayFormikState from '~/utils/DisplayFormikState'
import Button from '~/components/button/Button'
import Loading from '~/components/loading/Loading'

import './RedigerTestbruker.less'

export default class RedigerTestbruker extends Component {
	constructor() {
		super()
		this.AttributtManager = new AttributtManager()
	}

	componentDidMount() {
		this.props.getGruppe()
		this.props.getTestbruker()
		this.props.getSigrunTestbruker()
		this.props.getKrrTestbruker()
	}

	submit = values => {
		const { updateTestbruker, goBack, match, testbruker } = this.props

		updateTestbruker(
			values,
			this.AttributtManager.listEditableFlat(
				testbruker,
				match.params.ident,
				this._checkDataSources()
			)
		)
		goBack()
	}

	_checkDataSources = () => {
		const { testbruker, match } = this.props
		const { sigrunstub, krrstub } = testbruker

		const dataSources = [DataSource.TPSF]
		if (sigrunstub[match.params.ident] && sigrunstub[match.params.ident].length > 0) {
			dataSources.push(DataSource.SIGRUN)
		}

		if (krrstub[match.params.ident]) dataSources.push(DataSource.KRR)

		return dataSources
	}

	render() {
		const { testbruker, goBack, match } = this.props
		const { tpsf, sigrunstub, krrstub } = testbruker

		if (!tpsf || !sigrunstub || !krrstub) return null

		const dataSources = this._checkDataSources()
		const attributtListe = this.AttributtManager.listEditable(
			testbruker,
			match.params.ident,
			dataSources
		)
		const validations = this.AttributtManager.getValidationsForEdit(
			testbruker,
			match.params.ident,
			dataSources
		)
		const initialValues = this.AttributtManager.getInitialValuesForEditableItems(
			testbruker,
			match.params.ident,
			dataSources
		)

		return (
			<Formik
				onSubmit={this.submit}
				initialValues={initialValues}
				validationSchema={validations}
				render={formikProps => (
					<div>
						<h2>Rediger {`${tpsf[0].fornavn} ${tpsf[0].etternavn}`}</h2>
						{/* return <Loading label="Laster data fra TPSF" panel /> */}
						<FormEditor
							AttributtListe={attributtListe}
							FormikProps={formikProps}
							ClosePanels
							editMode
							getAttributtListByHovedkategori={
								this.AttributtManager.getAttributtListByHovedkategori
							}
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

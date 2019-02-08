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
	constructor(props) {
		super(props)
		this.AttributtManager = new AttributtManager()

		this.state = {
			addedAttributts: []
		}
	}

	componentDidMount() {
		this.props.getGruppe()
		this.props.getTestbruker()
		this.props.getSigrunTestbruker()
		this.props.getKrrTestbruker()
	}

	submit = (values, attributtListe) => {
		const { updateTestbruker, goBack } = this.props

		updateTestbruker(values, attributtListe)
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

	_onAddAttribute = attributeId => {
		this.setState({ addedAttributts: [...this.state.addedAttributts, attributeId] })
	}

	_getAttributtListeToEdit = (AttributtListe, AddedAttributts) => {
		let AttributtListeToEdit = []
		if (AddedAttributts && AddedAttributts.length > 0) {
			let tempElement = null
			AttributtListe.forEach(element => {
				tempElement = JSON.parse(JSON.stringify(element))
				AddedAttributts.forEach(addedElement => {
					if (tempElement.hovedKategori.id === addedElement.hovedKategori.id) {
						tempElement.items.forEach(subElement => {
							if (subElement.subKategori.id === addedElement.subKategori.id) {
								subElement.items.push(addedElement)
							}
						})
					}
				})
				AttributtListeToEdit.push(tempElement)
			})
			return AttributtListeToEdit
		}
		return AttributtListe
	}

	render() {
		const { testbruker, goBack, match } = this.props
		const { tpsf, sigrunstub, krrstub } = testbruker
		const { addedAttributts } = this.state

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

		console.log(initialValues, 'initialValues')
		const attributtListeToAdd = this.AttributtManager.listEditableWithoutValue(
			testbruker,
			match.params.ident,
			dataSources
		)

		const attributtListeToEdit = this._getAttributtListeToEdit(attributtListe, addedAttributts)

		return (
			<Formik
				onSubmit={values => this.submit(values, attributtListeToEdit)}
				initialValues={initialValues}
				validationSchema={validations}
				render={formikProps => (
					<div>
						<h2>Rediger {`${tpsf[0].fornavn} ${tpsf[0].etternavn}`}</h2>
						{/* return <Loading label="Laster data fra TPSF" panel /> */}
						<FormEditor
							AttributtListe={attributtListeToEdit}
							AttributtListeToAdd={attributtListeToAdd}
							AddedAttributts={addedAttributts}
							FormikProps={formikProps}
							ClosePanels
							editMode
							getAttributtListByHovedkategori={
								this.AttributtManager.getAttributtListByHovedkategori
							}
							onAddAttribute={this._onAddAttribute}
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

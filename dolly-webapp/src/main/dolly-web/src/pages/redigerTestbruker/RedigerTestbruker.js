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
			addedAttributes: []
		}
	}

	componentWillMount = async () => {
		this.props.getGruppe()
		const urlArray = this.props.match.params.datasources.split('&')

		await this.props.getTestbruker()
		urlArray.includes('sigr') && (await this.props.getSigrunTestbruker())
		urlArray.includes('krr') && (await this.props.getKrrTestbruker())
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
		if (sigrunstub && sigrunstub[match.params.ident] && sigrunstub[match.params.ident].length > 0) {
			dataSources.push(DataSource.SIGRUN)
		}

		if (krrstub && krrstub[match.params.ident]) dataSources.push(DataSource.KRR)

		return dataSources
	}

	_onAddAttribute = attributeId => {
		this.setState({ addedAttributes: [...this.state.addedAttributes, attributeId] })
	}

	_getAttributtListeToEdit = (AttributtListe, AddedAttributes, bestillinger) => {
		let finalAttributtListe = []
		let AttributtListeToEdit = []
		if (bestillinger.data) {
			const eksisterendeIdentBestilling = this._typeBestilling(bestillinger)
			eksisterendeIdentBestilling &&
				(finalAttributtListe = this._fjernSattForEksisterendeIdentAttr(AttributtListe))
		}

		finalAttributtListe.length < 1 &&
			AttributtListe.map(element => {
				finalAttributtListe.push(element)
			})

		if (AddedAttributes && AddedAttributes.length > 0) {
			let tempElement = null
			finalAttributtListe.forEach(element => {
				tempElement = JSON.parse(JSON.stringify(element))
				AddedAttributes.forEach(addedElement => {
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
		return finalAttributtListe
	}

	_typeBestilling = bestillinger => {
		let opprettetFraEksisterendeIdent = false
		bestillinger.data.map(bestilling => {
			if (bestilling.opprettFraIdenter) {
				const opprettFraIdenterArr = bestilling.opprettFraIdenter.split(',')
				opprettFraIdenterArr.map(ident => {
					ident === this.props.ident && (opprettetFraEksisterendeIdent = true)
				})
			}
		})
		return opprettetFraEksisterendeIdent
	}

	_fjernSattForEksisterendeIdentAttr = AttributtListe => {
		let finalAttributtListe = []
		let tempElement = null
		AttributtListe.map(element => {
			tempElement = Object.assign({}, element)
			tempElement.items.map((subElement, jdx) => {
				subElement.items.map((attr, idx) => {
					attr.sattForEksisterendeIdent && tempElement.items[jdx].items.splice(idx, 1)
				})
			})
			finalAttributtListe.push(tempElement)
		})
		return finalAttributtListe
	}

	render() {
		const { testbruker, goBack, match, bestillinger } = this.props
		const { tpsf, sigrunstub, krrstub } = testbruker
		const { addedAttributes } = this.state

		if (!tpsf) return null

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

		const attributtListeToAdd = this.AttributtManager.listEditableWithoutValue(
			testbruker,
			match.params.ident,
			dataSources
		)

		const attributtListeToEdit = this._getAttributtListeToEdit(
			attributtListe,
			addedAttributes,
			bestillinger
		)

		return (
			<Formik
				onSubmit={values => this.submit(values, attributtListeToEdit)}
				initialValues={initialValues}
				validationSchema={validations}
				enableReinitialize
				render={formikProps => (
					<div>
						<h2>Rediger {`${tpsf[0].fornavn} ${tpsf[0].etternavn}`}</h2>
						{/* return <Loading label="Laster data fra TPSF" panel /> */}
						<FormEditor
							AttributtListe={attributtListeToEdit}
							AttributtListeToAdd={attributtListeToAdd}
							AddedAttributes={addedAttributes}
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

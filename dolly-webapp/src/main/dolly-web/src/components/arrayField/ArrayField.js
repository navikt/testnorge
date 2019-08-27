import React, { Component, Fragment } from 'react'
import { DollyApi } from '~/service/Api'
import { Field } from 'formik'
import Button from '~/components/button/Button'
import InputSelector from '~/components/fields/InputSelector'
import _set from 'lodash/set'
import Loading from '~/components/loading/Loading'
import './arrayField.less'

export default class ArrayField extends Component {
	//OBS! Connector er ikke generisk. Se på den om komponent skal gjenbrukes
	state = {
		fieldListe: this.props.fieldListe || [],
		options: [],
		endretListe: true
	}

	componentDidMount() {
		const { fetchKodeverk } = this.props
		fetchKodeverk()
	}

	componentDidUpdate(prevProps) {
		this.state.fieldListe.length < 1 && this._initiering()
		this.state.options.length < 1 && this._alleOptions(this.props.item)
		const { endretListe } = this.state
		endretListe && this._fjerneOptions()
	}

	render() {
		const { item } = this.props
		const { fieldListe, options } = this.state
		const inputComponent = InputSelector(item.inputType)

		return (
			<div className="fieldOgVerdiliste">
				{options.length < 1 ? (
					<Loading onlySpinner />
				) : (
					<Field
						className={'input-boks'}
						key={item.key || item.id}
						name={item.id}
						label={item.label}
						component={inputComponent}
						size={item.size}
						onChange={this._handleChange}
						options={this.state.options}
						placeholder="Ikke spesifisert"
						isMultiple={true}
						{...item.inputTypeAttributes}
					/>
				)}
				{fieldListe.length > 0 &&
					fieldListe.map((field, idx) => {
						return this._renderValue(field.label, idx)
					})}
			</div>
		)
	}

	_handleChange = inputValue => {
		let { value, label } = inputValue
		const newFieldListe = [...this.state.fieldListe, inputValue]
		this.setState({ fieldListe: newFieldListe, endretListe: true })

		//Legger kun til value av inputValue i formikprops
		let valueListe = []
		newFieldListe.map(field => valueListe.push(field.value))

		_set(this.props.valgteVerdier, this.props.item.id, valueListe)
	}

	_renderValue = (field, idx) => {
		return (
			<Button className={'lukkbareKnapper'} key={idx} onClick={() => this._removeValue(idx)}>
				{field.toUpperCase()} <i className="fa fa-times" aria-hidden="true" />
			</Button>
		)
	}

	_removeValue = idx => {
		const copyListe = [...this.state.fieldListe]
		copyListe.splice(idx, 1)
		this.setState({ fieldListe: copyListe, endretListe: true })
		_set(this.props.valgteVerdier, this.props.item.id, copyListe)

		// Legge til valget i options
		this._leggeTilOptions(copyListe)
	}

	_initiering = () => {
		!this.state.endretListe &&
			this.props.fieldListe.length > 1 &&
			this.setState({ fieldListe: this.props.fieldListe })
	}

	_alleOptions = async item => {
		const showValueInLabel = item.apiKodeverkShowValueInLabel ? true : false

		this.props.kodeverkObjekt &&
			this.setState({
				options: DollyApi.Utils.NormalizeKodeverkForDropdown(
					{ data: this.props.kodeverkObjekt },
					showValueInLabel
				).options
			})
	}

	_fjerneOptions = () => {
		const fieldListe = this.state.fieldListe
		const optionsCopy = JSON.parse(JSON.stringify(this.state.options))

		fieldListe.length > 0 &&
			fieldListe.map(field => {
				const removeIndex = optionsCopy
					.map(function(item) {
						return item.value
					})
					.indexOf(field.value)
				optionsCopy.splice(removeIndex, 1)
			})
		optionsCopy.length !== this.state.options.length &&
			this.setState({ options: [...optionsCopy], endretListe: false })
	}

	_leggeTilOptions = fieldListe => {
		const { item } = this.props
		const showValueInLabel = item.apiKodeverkShowValueInLabel ? true : false

		const optionsCopy = DollyApi.Utils.NormalizeKodeverkForDropdown(
			{ data: this.props.kodeverkObjekt },
			showValueInLabel
		).options

		fieldListe.length > 0 &&
			fieldListe.map(field => {
				const removeIndex = optionsCopy
					.map(function(item) {
						return item.value
					})
					.indexOf(field.value)
				optionsCopy.splice(removeIndex, 1)
			})
		this.setState({ options: [...optionsCopy] })
	}
}

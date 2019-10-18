import React, { Component, Fragment } from 'react'
import { DollyApi } from '~/service/Api'
import { Field } from 'formik'
import Button from '~/components/ui/button/Button'
import InputSelector from '~/components/fields/InputSelector'
import _set from 'lodash/set'
import Loading from '~/components/ui/loading/Loading'
import './arrayField.less'

export default class ArrayField extends Component {
	//OBS! Connector er ikke generisk. Se på den om komponent skal gjenbrukes
	state = {
		fieldListe: this.props.fieldListe || [],
		options: [],
		endretListe: true
	}

	componentDidMount() {
		this.props.fetchKodeverk()
	}

	componentDidUpdate(prevProps) {
		const { endretListe, fieldListe, options } = this.state
		fieldListe.length < 1 && this._initiering()
		options.length < 1 && this._alleOptions(this.props.item)
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
					fieldListe.map((field, idx) => this._renderValue(field.label, idx))}
			</div>
		)
	}

	_handleChange = inputValue => {
		const { valgteVerdier, item } = this.props
		const newFieldListe = [...this.state.fieldListe, inputValue]
		this.setState({ fieldListe: newFieldListe, endretListe: true })

		_set(valgteVerdier, item.id, newFieldListe.map(field => field.value))
	}

	_renderValue = (field, idx) => {
		return (
			<Button className={'lukkbareKnapper'} key={idx} onClick={() => this._removeValue(idx)}>
				{field.toUpperCase()} <i className="fa fa-times" aria-hidden="true" />
			</Button>
		)
	}

	_removeValue = idx => {
		const { valgteVerdier, item } = this.props
		const copyListe = [...this.state.fieldListe].splice(idx, 1)
		this.setState({ fieldListe: copyListe, endretListe: true })
		_set(valgteVerdier, item.id, copyListe)

		// Legge til valget i options
		this._leggeTilOptions(copyListe)
	}

	_initiering = () => {
		!this.state.endretListe &&
			this.props.fieldListe.length > 1 &&
			this.setState({ fieldListe: this.props.fieldListe })
	}

	_alleOptions = async item => {
		const showValueInLabel = Boolean(item.apiKodeverkShowValueInLabel)

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

		// TODO: Duplikat av koden under
		fieldListe.length > 0 &&
			fieldListe.forEach(field => {
				const removeIndex = optionsCopy.map(item => item.value).indexOf(field.value)
				optionsCopy.splice(removeIndex, 1)
			})

		optionsCopy.length !== this.state.options.length &&
			this.setState({ options: [...optionsCopy], endretListe: false })
	}

	_leggeTilOptions = fieldListe => {
		const { item, kodeverkObjekt } = this.props
		const showValueInLabel = Boolean(item.apiKodeverkShowValueInLabel)

		const optionsCopy = DollyApi.Utils.NormalizeKodeverkForDropdown(
			{ data: kodeverkObjekt },
			showValueInLabel
		).options

		fieldListe.length > 0 &&
			fieldListe.forEach(field => {
				const removeIndex = optionsCopy.map(item => item.value).indexOf(field.value)
				optionsCopy.splice(removeIndex, 1)
			})

		this.setState({ options: [...optionsCopy] })
	}
}

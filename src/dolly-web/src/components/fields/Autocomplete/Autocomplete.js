import React, { Component } from 'react'
import './Autocomplete.less'
import Select from 'react-select'

export default class Autocomplete extends Component {
	constructor(props, context) {
		super(props, context)
		this.handleChange = this.handleChange.bind(this)
		this.optionsRenderer = this.optionsRenderer.bind(this)
		this.formatKodeverk = this.formatKodeverk.bind(this)
		this.handleOnInputChange = this.handleOnInputChange.bind(this)
		Autocomplete.capitalizeFirstLetter = Autocomplete.capitalizeFirstLetter.bind(this)

		this.customKodeverk = this.formatKodeverk(this.props.kodeverk)

		this.state = {
			inputValue: '',
			fieldValue: ''
		}
	}

	static capitalizeFirstLetter(string) {
		return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase()
	}

	formatKodeverk(kodeverk) {
		let customKodeverk = []
		kodeverk.forEach(kode => {
			customKodeverk.push({
				value: kode.navn,
				label: kode.navn + ' - ' + Autocomplete.capitalizeFirstLetter(kode.term)
			})
		})

		return customKodeverk
	}

	optionsRenderer = option => {
		let markup = { __html: option.label }

		if (this.state.fieldValue) {
			let re = new RegExp('(' + this.state.fieldValue + ')', 'gi')
			markup.__html = option.label.replace(re, '<b>$1</b>')
		}

		return (
			<div
				className="option-container"
				data-flex
				data-layout="row"
				data-layout-align="start center"
			>
				<div className="option-text" dangerouslySetInnerHTML={markup} />
			</div>
		)
	}

	handleChange = selectedOption => {
		if (selectedOption) {
			this.props.onSelectedValue(
				Object.assign(
					{},
					{
						navn: selectedOption.value,
						term: selectedOption.label
					}
				)
			)

			this.setState({ inputValue: selectedOption.value })
		} else {
			this.setState({ inputValue: '' })
		}
	}

	handleOnInputChange(value) {
		this.setState({ fieldValue: value })
	}

	render() {
		const { id, label } = this.props

		return (
			<div className="skjemaelement" id="autocomplete-container">
				<label className="skjemaelement__label">{label} </label>
				<div id={id} className="input-autocomplete">
					<Select
						options={this.customKodeverk}
						onChange={this.handleChange}
						onInputChange={this.handleOnInputChange}
						value={this.state.inputValue}
						tabSelectsValue={false}
						matchProp={'any'}
						matchPos={'any'}
						optionRenderer={this.optionsRenderer}
						arrowRenderer={() => {}}
						ignoreCase={true}
						clearable={true}
						openOnFocus={true}
						clearValueText="Slett input"
						noResultsText={'Finner ikke "' + this.state.inputValue + '"'}
						placeholder={''}
					/>
				</div>
			</div>
		)
	}
}

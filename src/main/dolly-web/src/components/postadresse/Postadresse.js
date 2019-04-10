import React, { Component, Fragment } from 'react'
import { DollyApi } from '~/service/Api'
import './Postadresse.less'
import InputSelector from '~/components/fields/InputSelector'
import { Field } from 'formik'
import Adressesjekk from '~/utils/SjekkPostadresse'

export default class Postadresse extends Component {
	state = {
		gyldig: true
	}

	render() {
		const items = this.props.items
		let adressefelter = []

		items.map(item => item.id !== 'postLand' && adressefelter.push(item))
		return (
			<div className="subkategori">
				<div className="subkategori-field-group">
					<div className="subkategori-field-group">
						{items.map(item => item.id === 'postLand' && this.renderFieldComponent(item))}
					</div>
					<div className="postadresse-group">
						{adressefelter.map(item => this.renderFieldComponent(item))}
					</div>
				</div>
				<div>
					{!this.state.gyldig && (
						<p>I norske postadresser må siste utfylte linje starte med et gyldig postnummer</p>
					)}
				</div>
			</div>
		)
	}

	renderFieldComponent = item => {
		if (!item.inputType) return null
		const InputComponent = InputSelector(item.inputType)
		const componentProps = this.extraComponentProps(item)
		let label = ''

		if (item.label === 'Land') {
			label = 'Land'
		} else if (item.label === 'Adresselinje 1') {
			label = 'Adresse'
		}

		return (
			<Field
				key={item.key || item.id}
				name={item.id}
				label={label}
				component={InputComponent}
				onBlur={this.checkValues}
				size={item.size}
				options={item.options}
				{...componentProps}
				{...item.inputTypeAttributes}
			/>
		)
	}

	extraComponentProps = item => {
		if (item.inputType === 'select') {
			const placeholder = !item.validation ? 'Ikke spesifisert' : 'Velg...'

			if (item.apiKodeverkId) {
				const showValueInLabel = item.apiKodeverkShowValueInLabel ? true : false
				return {
					placeholder: placeholder,
					loadOptions: async () => {
						const res = await DollyApi.getKodeverkByNavn(item.apiKodeverkId)
						return DollyApi.Utils.NormalizeKodeverkForDropdown(res, showValueInLabel)
					}
				}
			} else {
				return {
					placeholder: placeholder,
					options: item.options
				}
			}
		}
		return { placeholder: item.label }
	}

	checkValues = async () => {
		const values = this.props.formikProps.values

		if (values['postLand'] === '') {
			values['postLand'] = 'NOR'
		}

		if ((await Adressesjekk.sjekkPostadresse(values)) === false) {
			this.setState({ gyldig: false })
		} else this.setState({ gyldig: true })
	}
}

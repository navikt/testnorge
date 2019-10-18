import React, { Component, Fragment } from 'react'
import { Field } from 'formik'
import { DollyApi } from '~/service/Api'
import InputSelector from '~/components/fields/InputSelector'
import { sjekkPostadresse } from '~/utils/SjekkPostadresse'
import Loading from '~/components/ui/loading/Loading'
import './Postadresse.less'

export default class Postadresse extends Component {
	constructor(props) {
		super(props)
		this._isMounted = false
	}

	state = {
		gyldig: true,
		isChecking: false
	}

	componentDidMount() {
		this._isMounted = true
	}

	componentWillUnmount() {
		this._isMounted = false
	}

	render() {
		const items = this.props.items
		let adressefelter = []

		items.map(item => item.id !== 'postLand' && adressefelter.push(item))
		return (
			<div className="postadresse_subkategori">
				<div className="postadresse_subkategori-field-group">
					<div className="postadresse_subkategori-field-group">
						{items.map(item => item.id === 'postLand' && this.renderFieldComponent(item))}
					</div>
					<div className="postadresse-group">
						{adressefelter.map(item => this.renderFieldComponent(item))}
						{!this.state.gyldig &&
							this.state.isChecking && (
								<div>
									<Loading label="Validerer postadresse..." />
								</div>
							)}
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
		this.setState({ isChecking: true })
		const values = this.props.formikProps.values

		if (values['postLand'] === '') {
			values['postLand'] = 'NOR'
		}

		if ((await sjekkPostadresse(values)) === false) {
			if (this._isMounted) {
				this.setState({ gyldig: false })
				this.setState({ isChecking: false })
			}
		} else {
			if (this._isMounted) {
				this.setState({ gyldig: true })
				this.setState({ isChecking: false })
			}
		}
	}
}

import React, { Component, Fragment } from 'react'
import { DollyApi } from '~/service/Api'
import InputSelector from '~/components/fields/InputSelector'
import { Field } from 'formik'
import Button from '~/components/button/Button'

export default class UtenFastBopel extends Component {
	// state = {
	// 	gyldig: true
	// }

	render() {
		// console.log('this :', this)
		const item = this.props.item
		const valgteVerdier = this.props.valgteVerdier
		const InputComponent = InputSelector(item.inputType)
		const InputComponentKommunenr = InputSelector('InputType.Select')
		const componentProps = this.extraComponentProps(item)
		const componentPropsKommunenr = this.extraComponentPropsKommunenr()

		// console.log('item :', item)
		// console.log('item.inputTypeAttributes :', item.inputTypeAttributes)

		return (
			<div>
				<Field
					key={item.key || item.id}
					name={item.id}
					label={item.label}
					component={InputComponent}
					size={item.size}
					{...componentProps}
					{...item.inputTypeAttributes}
				/>
				{valgteVerdier.spesreg === 'UFB' && (
					<div>
						<Button
							className="flexbox--align-center field-group-add"
							kind="add-circle"
							// onClick={createDefaultObject}
						>
							DISKRESJONSKODE
						</Button>
						<Field
							key={'boadresse_kommunenr'}
							name={'boadresse_kommunenr'}
							label={'Kommunenummer'}
							component={InputComponentKommunenr}
							size={item.size}
							{...componentPropsKommunenr}
							// {...item.inputTypeAttributes}
						/>
						{/* <p>Velg kommunenummer</p> */}
					</div>
				)}
			</div>
		)

		// const items = this.props.items
		// let adressefelter = []

		// items.map(item => item.id !== 'postLand' && adressefelter.push(item))
		// return (
		// 	<div className="subkategori">
		// 		<div className="subkategori-field-group">
		// 			<div className="subkategori-field-group">
		// 				{items.map(item => item.id === 'postLand' && this.renderFieldComponent(item))}
		// 			</div>
		// 			<div className="postadresse-group">
		// 				{adressefelter.map(item => this.renderFieldComponent(item))}
		// 			</div>
		// 		</div>
		// 		<div>
		// 			{!this.state.gyldig && (
		// 				<p>I norske postadresser må siste utfylte linje starte med et gyldig postnummer</p>
		// 			)}
		// 		</div>
		// 	</div>
		// )
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

	extraComponentPropsKommunenr = () => {
		return {
			placeholder: 'Velg kommunenummer...',
			loadOptions: async () => {
				const res = await DollyApi.getKodeverkByNavn('Kommuner')
				return DollyApi.Utils.NormalizeKodeverkForDropdown(res, false)
			}
			// loadOptions: this._kodeverk()
		}
	}

	// _kodeverk = () => {
	// 	return DollyApi.getKodeverkByNavn('Kommuner').then(res => {
	// 		return {
	// 			options: res.data.koder.map(kode => ({
	// 				label: `${kode.value} - ${kode.label}`,
	// 				value: kode.value,
	// 				id: 'boadresse_kommunenr'
	// 			}))
	// 		}
	// 	})
	// }

	// renderFieldComponent = item => {
	// 	if (!item.inputType) return null
	// 	const InputComponent = InputSelector(item.inputType)
	// 	const componentProps = this.extraComponentProps(item)
	// 	let label = ''

	// 	if (item.label === 'Land') {
	// 		label = 'Land'
	// 	} else if (item.label === 'Adresselinje 1') {
	// 		label = 'Adresse'
	// 	}

	// 	return (
	// 		<Field
	// 			key={item.key || item.id}
	// 			name={item.id}
	// 			label={label}
	// 			component={InputComponent}
	// 			onBlur={this.checkValues}
	// 			size={item.size}
	// 			options={item.options}
	// 			{...componentProps}
	// 			{...item.inputTypeAttributes}
	// 		/>
	// 	)
	// }

	// checkValues = async () => {
	// 	const values = this.props.formikProps.values

	// 	if (values['postLand'] === '') {
	// 		values['postLand'] = 'NOR'
	// 	}

	// 	if ((await Adressesjekk.sjekkPostadresse(values)) === false) {
	// 		this.setState({ gyldig: false })
	// 	} else this.setState({ gyldig: true })
	// }
}

import React, { Component, Fragment } from 'react'
import { DollyApi } from '~/service/Api'
import InputSelector from '~/components/fields/InputSelector'
import { Field } from 'formik'
import Button from '~/components/button/Button'
import { FormikDollySelect } from '../fields/Select/Select'
import './UtenFastBopel.less'


export default class UtenFastBopel extends Component {
	state = {
		harEkstraDiskresjonskode: false
	}

	componentDidMount() {
		const { values, valgteVerdier } = this.props
		if (values.spesreg && values.spesreg2) {
			this.setState({ harEkstraDiskresjonskode: true })
		}

		if (values.utenFastBopel === true) {
			valgteVerdier.spesreg = 'UFB'
		}
	}

	componentDidUpdate(prevProps) {
		const ufbKmnrProps = this.props.valgteVerdier.ufb_kommunenr
		if (ufbKmnrProps && !this.props.attributeIds.includes('ufb_kommunenr')) {
			this.props.toggleAttribute('ufb_kommunenr')
		}

		if (!ufbKmnrProps && this.props.attributeIds.includes('ufb_kommunenr')) {
			this.props.toggleAttribute('ufb_kommunenr')
		}
	}

	render() {
		const { values } = this.props
		console.log('this.props------- :', this.props);
		const item = this.props.item
		const valgteVerdier = this.props.valgteVerdier
		const InputComponent = InputSelector(item.inputType)
		const componentProps = this._extraComponentProps(item, 'm_ufb')
		const componentPropsUtenUfb = this._extraComponentProps(item, 'u_ufb')

		item.id === 'utenFastBopel' && (valgteVerdier.spesreg = 'UFB')

		values.spesreg2 && (valgteVerdier['spesreg2'] = values.spesreg2)

		return (
			<div className="diskresjonskoder">
				<div>
					<Field
						className={'input-boks'}
						key={'spesreg'}
						name={'spesreg'}
						label={'Diskresjonskoder'}
						component={InputComponent}
						size={item.size}
						hoydeOptions={'large'}
						{...componentProps}
						{...item.inputTypeAttributes}
					/>
					{valgteVerdier.spesreg === 'UFB' &&
						!this.state.harEkstraDiskresjonskode && (
							<Button
								className="flexbox--align-center field-group-add"
								kind="add-circle"
								onClick={this._onClickEkstraDiskresjonskode}
							>
								DISKRESJONSKODE
							</Button>
						)}
					{valgteVerdier.spesreg === 'UFB' &&
						this.state.harEkstraDiskresjonskode && (
							<div className="ekstra-diskresjonskode">
								<Field
									key={'spesreg'}
									name={'spesreg2'}
									label={''}
									component={InputComponent}
									size={item.size}
									{...componentPropsUtenUfb}
								/>
								<Button className="x-knapp" kind="remove-circle" onClick={this._onRemoveButton} />
							</div>
						)}
				</div>

				{valgteVerdier.spesreg === 'UFB' && (
					<Field
						key={'ufb_kommunenr'}
						name={'ufb_kommunenr'}
						label={'Kommunenummer'}
						placeholder={'Velg kommunenummer...'}
						component={FormikDollySelect}
						size={item.size}
						loadOptions={this._extraComponentPropsKommunenr}
					/>
				)}
			</div>
		)
	}

	_onClickEkstraDiskresjonskode = () => {
		const { attributeIds, valgteVerdier } = this.props
		this.setState({ harEkstraDiskresjonskode: true })
		attributeIds.push('utenFastBopel')
		valgteVerdier['utenFastBopel'] = true
	}

	_onRemoveButton = () => {
		const { values, valgteVerdier } = this.props
		this.setState({ harEkstraDiskresjonskode: false })
		values.spesreg2 = ''
		valgteVerdier.spesreg2 = ''
	}

	_extraComponentProps = (item, type) => {
	console.log('this.props xxxxxxxx:', this.props);
		console.log('item xxxxxxx:', item);
		if (item.inputType === 'select') {
			const placeholder = !item.validation ? 'Ikke spesifisert' : 'Velg...'

			if (item.apiKodeverkId) {
				const showValueInLabel = item.apiKodeverkShowValueInLabel ? true : false
				return {
					placeholder: placeholder,
					loadOptions: async () => {
						const res = await DollyApi.getKodeverkByNavn(item.apiKodeverkId)
						if (type === 'm_ufb') {
							return DollyApi.Utils.NormalizeKodeverkForDropdown(res, showValueInLabel)
						} else if (type === 'u_ufb') {
							return DollyApi.Utils.NormalizeKodeverkForDropdownUtenUfb(res, showValueInLabel)
						}
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

	_extraComponentPropsKommunenr = () => {
		return DollyApi.getKodeverkByNavn('Kommuner').then(res => {
			return {
				options: res.data.koder.map(kode => ({
					label: `${kode.value} - ${kode.label}`,
					value: kode.value,
					id: 'boadresse_kommunenr'
				}))
			}
		})
	}
}

import React, { PureComponent, Fragment } from 'react'
import { Field, FieldArray } from 'formik'
import { DollyApi } from '~/service/Api'
import Panel from '~/components/panel/Panel'
import InputSelector from '~/components/fields/InputSelector'
import Button from '~/components/button/Button'

export default class FormEditor extends PureComponent {
	renderHovedKategori = ({ hovedKategori, items }, formikProps, closePanels) => {
		return (
			<Panel
				key={hovedKategori.id}
				heading={<h3>{hovedKategori.navn}</h3>}
				startOpen={!closePanels}
			>
				{items.map((item, idx) => {
					return item.subKategori && item.subKategori.multiple
						? this.renderSubKategoriAsFieldArray(item, formikProps)
						: this.renderFieldContainer(
								item.subKategori ? item.subKategori.navn : null,
								item.items,
								idx
						  )
				})}
			</Panel>
		)
	}
	renderSubKategoriAsFieldArray = ({ subKategori, items }, formikProps) => {
		const subId = subKategori.id
		return (
			<div className="subkategori" key={subId}>
				<FieldArray
					name={subId}
					render={arrayHelpers => {
						const defs = items.reduce((prev, curr) => ({ ...prev, [curr.id]: '' }), {})
						const createDefaultObject = () => arrayHelpers.push({ ...defs })
						return (
							<Fragment>
								<h4>
									{subKategori.navn}{' '}
									<Button
										className="field-group-add"
										kind="add-circle"
										onClick={createDefaultObject}
									/>
								</h4>
								{formikProps.values[subId] && formikProps.values[subId].length > 0 ? (
									formikProps.values[subId].map((faKey, idx) => {
										return (
											<div key={idx}>
												<div className="subkategori-field-group">
													{items.map(item => {
														// Add subKategori to ID
														const fakeItem = {
															...item,
															id: `${subId}[${idx}]${item.id}`
														}
														return this.renderFieldComponent(fakeItem)
													})}
													<Button
														className="field-group-remove"
														kind="remove-circle"
														onClick={e => arrayHelpers.remove(idx)}
													/>
												</div>
											</div>
										)
									})
								) : (
									<span>Ingen barn lagt til</span>
								)}
							</Fragment>
						)
					}}
				/>
			</div>
		)
	}

	renderFieldContainer = (header, items, uniqueId) => (
		<div className="subkategori" key={uniqueId}>
			{header && <h4>{header}</h4>}
			<div className="subkategori-field-group">
				{items.map(item => this.renderFieldComponent(item))}
			</div>
		</div>
	)

	renderFieldComponent = item => {
		const InputComponent = InputSelector(item.inputType)
		const componentProps = this.extraComponentProps(item)

		return (
			<Field
				key={item.id}
				name={item.id}
				label={item.label}
				component={InputComponent}
				{...componentProps}
			/>
		)
	}

	extraComponentProps = item => {
		switch (item.inputType) {
			case 'select': {
				if (item.apiKodeverkId) {
					return {
						loadOptions: () =>
							DollyApi.getKodeverkByNavn(item.apiKodeverkId).then(
								DollyApi.Utils.NormalizeKodeverkForDropdown
							)
					}
				} else {
					return {
						options: item.options
					}
				}
			}
			case 'number': {
				return {
					type: item.inputType
				}
			}
			default:
				return {}
		}
	}

	render() {
		const { AttributtListe, FormikProps, ClosePanels } = this.props

		return AttributtListe.map(hovedKategori =>
			this.renderHovedKategori(hovedKategori, FormikProps, ClosePanels)
		)
	}
}

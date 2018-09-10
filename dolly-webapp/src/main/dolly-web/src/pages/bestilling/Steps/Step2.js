import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import NavigationConnector from '../Navigation/NavigationConnector'
import Panel from '~/components/panel/Panel'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import { Formik, Field, FieldArray } from 'formik'
import { AttributtManager } from '~/service/Kodeverk'
import DisplayFormikState from '~/utils/DisplayFormikState'
import InputSelector from '~/components/fields/InputSelector'
import { extraComponentProps } from '../Utils'

export default class Step2 extends PureComponent {
	static propTypes = {
		identtype: PropTypes.string,
		antall: PropTypes.number,
		selectedAttributeIds: PropTypes.array,
		setValues: PropTypes.func
	}

	constructor(props) {
		super(props)
		this.AttributtManager = new AttributtManager()
		this.AttributtListe = this.AttributtManager.listSelectedByGroup(props.selectedAttributeIds)
		this.ValidationListe = this.AttributtManager.getValidations(props.selectedAttributeIds)
	}

	submit = values => {
		this.props.setValues(values)
	}

	renderForm = formikProps => {
		return this.AttributtListe.map(hovedKategori =>
			this.renderHovedKategori(hovedKategori, formikProps)
		)
	}

	renderHovedKategori = ({ hovedKategori, items }, formikProps) => {
		return (
			<Panel key={hovedKategori.id} heading={<h3>{hovedKategori.id}</h3>} startOpen>
				{items.map(
					item =>
						item.subKategori.multiple
							? this.renderSubKategoriAsFieldArray(item, formikProps)
							: this.renderSubKategori(item, formikProps)
				)}
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
									{subId} <button onClick={createDefaultObject}>+</button>
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
												</div>
												<button onClick={e => arrayHelpers.remove(idx)}>fjern</button>
											</div>
										)
									})
								) : (
									<span>ingen</span>
								)}
							</Fragment>
						)
					}}
				/>
			</div>
		)
	}

	renderSubKategori = ({ subKategori, items }) => (
		<div className="subkategori" key={subKategori.navn}>
			<h4>{subKategori.navn}</h4>
			<div className="subkategori-field-group">
				{items.map(item => this.renderFieldComponent(item))}
			</div>
		</div>
	)

	renderFieldComponent = item => {
		const InputComponent = InputSelector(item.inputType)
		const componentProps = extraComponentProps(item)

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

	onClickPrevious = values => {
		this.props.setValuesAndGoBack(values)
	}

	render() {
		const { identtype, antall, values } = this.props

		return (
			<div className="bestilling-step2">
				<div className="content-header">
					<Overskrift label="Velg verdier" />
				</div>

				<div className="grunnoppsett">
					<StaticValue header="TYPE" value={identtype} />
					<StaticValue header="ANTALL PERSONER" value={antall.toString()} />
				</div>

				<Formik
					onSubmit={this.submit}
					initialValues={values}
					validationSchema={this.ValidationListe}
					render={formikProps => (
						<Fragment>
							{this.renderForm(formikProps)}
							<NavigationConnector
								onClickNext={formikProps.submitForm}
								onClickPrevious={() => this.props.setValuesAndGoBack(formikProps.values)}
							/>
							<DisplayFormikState {...formikProps} />
						</Fragment>
					)}
				/>
			</div>
		)
	}
}

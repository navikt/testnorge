import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import NavigationConnector from '../Navigation/NavigationConnector'
import Panel from '~/components/panel/Panel'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import { Formik, Field } from 'formik'
import { AttributtManager } from '~/service/Kodeverk'
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
		this.InitialValues = this.AttributtManager.getInitialValues(props.selectedAttributeIds)
	}

	submit = values => {
		this.props.setValues(values)
	}

	renderForm = formikProps => {
		return this.AttributtListe.map(hovedKategori => this.renderHovedKategori(hovedKategori))
	}

	renderHovedKategori = ({ hovedKategori, items }) => {
		return (
			<Panel key={hovedKategori.navn} heading={<h3>{hovedKategori.navn}</h3>} startOpen>
				{items.map(subKategori => this.renderSubKategori(subKategori))}
			</Panel>
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

	render() {
		const { identtype, antall } = this.props

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
					initialValues={this.InitialValues}
					onSubmit={this.submit}
					validationSchema={this.ValidationListe}
					render={formikProps => (
						<Fragment>
							{this.renderForm(formikProps)}
							<NavigationConnector onClickNext={formikProps.submitForm} />
						</Fragment>
					)}
				/>
			</div>
		)
	}
}

import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import NavigationConnector from '../Navigation/NavigationConnector'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import { Formik } from 'formik'
import { AttributtManager } from '~/service/Kodeverk'
import FormEditor from '~/components/formEditor/FormEditor'
import ContentContainer from '~/components/contentContainer/ContentContainer'
import Icon from '~/components/icon/Icon'
import DisplayFormikState from '~/utils/DisplayFormikState'
import BestillingMapper from '~/utils/BestillingMapper'

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
		this.AttributtListe = this.AttributtManager.listSelectedAttributesForValueSelection(
			props.selectedAttributeIds
		)
		this.ValidationListe = this.AttributtManager.getValidations(props.selectedAttributeIds)

		// TODO: Alex - initial values for child attributts with subItems
		this.InitialValues = this.AttributtManager.getInitialValues(
			props.selectedAttributeIds,
			props.values
		)
	}

	submit = values => {
		this.props.setValues({ values })
	}

	onClickPrevious = values => {
		this.props.setValues({ values, goBack: true })
	}

	render() {
		const {
			identtype,
			antall,
			selectedAttributeIds,
			identOpprettesFra,
			eksisterendeIdentListe
		} = this.props

		return (
			<div className="bestilling-step2">
				<div className="content-header">
					<Overskrift label="Velg verdier" />
				</div>
				{identOpprettesFra === BestillingMapper() ? (
					<div className="grunnoppsett">
						<StaticValue header="TYPE" value={identtype} />
						<StaticValue header="ANTALL PERSONER" value={antall.toString()} />
					</div>
				) : (
					<div className="grunnoppsett">
						<StaticValue header="IDENTER" value={eksisterendeIdentListe} />
					</div>
				)}
				<Formik
					onSubmit={this.submit}
					initialValues={this.InitialValues}
					validationSchema={this.ValidationListe}
					render={formikProps => (
						<Fragment>
							{selectedAttributeIds.length === 0 ? (
								<ContentContainer className="">
									<Icon kind="report-problem-circle" />Du har valgt ingen attributter. Dolly vil
									opprette testpersoner med tilfeldige verdier.
								</ContentContainer>
							) : (
								<FormEditor
									AttributtListe={this.AttributtListe}
									FormikProps={formikProps}
									getAttributtListByHovedkategori={
										this.AttributtManager.getAttributtListByHovedkategori
									}
								/>
							)}

							<NavigationConnector
								onClickNext={formikProps.submitForm}
								onClickPrevious={() => this.onClickPrevious(formikProps.values)}
							/>
							{/* // Uncomment for debug formik */}
							{/* <DisplayFormikState {...formikProps} /> */}
						</Fragment>
					)}
				/>
			</div>
		)
	}
}

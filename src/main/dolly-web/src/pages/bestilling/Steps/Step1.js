import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import * as yup from 'yup'
import NavigationConnector from '~/pages/bestilling/Navigation/NavigationConnector'
import { Field, withFormik } from 'formik'
import { animateScroll } from 'react-scroll'
import { Radio } from 'nav-frontend-skjema'
import '~/pages/bestilling/Bestilling.less'
import NyIdent from '~/components/opprettIdent/NyIdent.js'
import EksisterendeIdentConnector from '~/components/opprettIdent/EksisterendeIdentConnector'
import BestillingMapper from '~/utils/BestillingMapper'
import { FormikDollySelect } from '~/components/fields/Select/Select'

class Step1 extends Component {
	static propTypes = {
		identtype: PropTypes.string,
		antall: PropTypes.number,
		selectedAttributeIds: PropTypes.array,
		identOpprettesFra: PropTypes.string,
		startBestilling: PropTypes.func,
		toggleAttributeSelection: PropTypes.func,
		setIdentOpprettesFra: PropTypes.func
	}

	_onSubmitForm = () => {
		this.props.submitForm()
		animateScroll.scrollToTop({ duration: 250 })
	}

	_renderRadioBtn = (checkedType, label) => {
		console.log('identOpprettesFra :', this.props.identOpprettesFra)
		return (
			<Radio
				checked={this.props.identOpprettesFra === checkedType}
				label={label}
				name={label}
				onChange={() => this._chooseType(checkedType)}
			/>
		)
	}
	_chooseType = checkedType => {
		this.props.setIdentOpprettesFra(checkedType)
	}

	render() {
		const { identOpprettesFra, eksisterendeIdentListe } = this.props

		return (
			<div className="bestilling-step1">
				<div className="flexbox--space">
					<Overskrift label="Velg egenskaper" />
				</div>
				<form className="flexbox">
					<span className="bestilling-page radiobuttons">
						{this._renderRadioBtn(BestillingMapper(), 'NY TESTIDENT')}
					</span>
					<span className="bestilling-page radiobuttons">
						{this._renderRadioBtn(BestillingMapper('EKSIDENT'), 'EKSISTERENDE TESTIDENT')}
					</span>
					<span className="bestilling-page radiobuttons">
						{this._renderRadioBtn(BestillingMapper('MAL'), 'MALBESTILLING')}
					</span>
				</form>
				{this._renderAttributtVelger()}
				<NavigationConnector
					onClickNext={this._onSubmitForm}
					eksisterendeIdentListe={eksisterendeIdentListe}
					identOpprettesFra={identOpprettesFra}
				/>
			</div>
		)
	}

	_renderAttributtVelger = () => {
		const {
			selectedAttributeIds,
			toggleAttributeSelection,
			uncheckAllAttributes,
			checkAttributeArray,
			uncheckAttributeArray,
			identOpprettesFra,
			eksisterendeIdentListe
		} = this.props

		switch (identOpprettesFra) {
			case BestillingMapper():
				return (
					<NyIdent
						selectedAttributeIds={selectedAttributeIds}
						toggleAttributeSelection={toggleAttributeSelection}
						uncheckAllAttributes={uncheckAllAttributes}
						checkAttributeArray={checkAttributeArray}
						uncheckAttributeArray={uncheckAttributeArray}
					/>
				)
			case BestillingMapper('EKSIDENT'):
				return (
					<EksisterendeIdentConnector
						selectedAttributeIds={selectedAttributeIds}
						toggleAttributeSelection={toggleAttributeSelection}
						uncheckAllAttributes={uncheckAllAttributes}
						checkAttributeArray={checkAttributeArray}
						uncheckAttributeArray={uncheckAttributeArray}
						identOpprettesFra={identOpprettesFra}
					/>
				)
			case BestillingMapper('MAL'):
				return (
					<Field
						name="maler"
						label="Velg en mal"
						className="dollyselect medium"
						component={FormikDollySelect}
						options={[{ value: 'Beregnet skatt', label: 'Beregnet ef' }]}
					/>
				)
			default:
				break
		}
	}
}

export default withFormik({
	displayName: 'BestillingStep1',
	mapPropsToValues: props => ({
		identtype: props.identtype || 'FNR', // default to FNR
		antall: props.antall
	}),
	validationSchema: yup.object().shape({
		antall: yup
			.number()
			.positive('Må være et positivt tall')
			.min(1, 'Må minst opprette 1 testperson')
			// .max(5, 'Maks 5 personer i første omgang')
			.required('Oppgi antall testbrukere'),
		identtype: yup.string().required('Velg en identtype')
	}),
	handleSubmit: (values, { props, setSubmitting, setErrors }) => {
		props.startBestilling(values)
	}
})(Step1)

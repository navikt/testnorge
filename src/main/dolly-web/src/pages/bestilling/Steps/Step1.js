import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Overskrift from '~/components/overskrift/Overskrift'
import * as yup from 'yup'
import NavigationConnector from '~/pages/bestilling/Navigation/NavigationConnector'
import { Field, withFormik } from 'formik'
import { animateScroll } from 'react-scroll'
import { Radio } from 'nav-frontend-skjema'
import '~/pages/bestilling/Bestilling.less'
import NyIdentConnector from './NyIdent/NyIdentConnector.js'
import EksisterendeIdentConnector from './EksisterendeIdent/EksisterendeIdentConnector'
// import MalBestillingConnector from '~/components/opprettIdent/MalBestillingConnector'
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
		const { identOpprettesFra, eksisterendeIdentListe, antall } = this.props

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
			values,
			resetForm
		} = this.props

		// console.log('values :', values)

		switch (identOpprettesFra) {
			case BestillingMapper():
				return (
					<NyIdentConnector
						selectedAttributeIds={selectedAttributeIds}
						toggleAttributeSelection={toggleAttributeSelection}
						uncheckAllAttributes={uncheckAllAttributes}
						checkAttributeArray={checkAttributeArray}
						uncheckAttributeArray={uncheckAttributeArray}
						malBestillingNavn={values.mal}
						resetForm={resetForm}
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
			// case BestillingMapper('MAL'):
			// 	return (
			// 		<MalBestillingConnector
			// 			selectedAttributeIds={selectedAttributeIds}
			// 			toggleAttributeSelection={toggleAttributeSelection}
			// 			uncheckAllAttributes={uncheckAllAttributes}
			// 			checkAttributeArray={checkAttributeArray}
			// 			uncheckAttributeArray={uncheckAttributeArray}
			// 			mal={values.mal}
			// 		/>
			// 	)
			default:
				break
		}
	}
}

export default withFormik({
	displayName: 'BestillingStep1',
	enableReinitialize: true,
	mapPropsToValues: props => {
		// console.log('NY props', props)
		return {
			identtype: props.identtype || 'FNR', // default to FNR
			antall: props.antall,
			mal: props.currentMal
		}
	},
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
		console.log(values, 'values')
		props.startBestilling(values)
	}
})(Step1)

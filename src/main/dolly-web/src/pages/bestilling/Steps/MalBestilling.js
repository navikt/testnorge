import React, { Component, Fragment } from 'react'
import '~/pages/bestilling/Bestilling.less'
import AttributtVelgerConnector from '~/components/attributtVelger/AttributtVelgerConnector'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { Field, withFormik } from 'formik'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import ContentTooltip from '~/components/contentTooltip/ContentTooltip'
import NyIdent from '~/components/opprettIdent/NyIdent.js'

export default class MalBestilling extends Component {
	state = {
		malOptions: {}
	}

	componentDidMount() {
		this.props.getBestillingMaler()
		this.props.maler.length > 0 && this._formatMalerOptions()
	}

	componentDidUpdate(prevProps) {
		console.log('updated')

		if (this.props.mal !== prevProps.mal) {
			this.props.checkAttributeArray(['statsborgerskap', 'spesreg'])
		}
	}

	render() {
		const {
			selectedAttributeIds,
			toggleAttributeSelection,
			uncheckAllAttributes,
			checkAttributeArray,
			uncheckAttributeArray,
			maler,
			mal
		} = this.props

		// TODO: fetching loading
		if (maler.length === 0) return <div>Ingen maler</div>
		console.log('mal', this.props.mal)
		return (
			<Fragment>
				<div className="flexbox">
					<Field
						name="mal"
						label="Velg mal"
						className="dollyselect medium"
						component={FormikDollySelect}
						options={this._formatMalerOptions(maler)}
					/>
				</div>
				<div>{mal} </div>
				{mal && this._showMalSpecs(maler, mal)
				// <NyIdent
				// 	selectedAttributeIds={selectedAttributeIds}
				// 	toggleAttributeSelection={toggleAttributeSelection}
				// 	uncheckAllAttributes={uncheckAllAttributes}
				// 	checkAttributeArray={checkAttributeArray}
				// 	uncheckAttributeArray={uncheckAttributeArray}
				// />
				}
			</Fragment>
		)
	}

	_showMalSpecs = (maler, mal) => {
		console.log(mal, 'mal')
		const res = maler.find(m => m.malBestillingNavn === mal)

		return (
			<div>
				<p>{res.tpsfKriterier}</p>
				<p>{res.bestKriterier}</p>
			</div>
		)
	}

	_formatMalerOptions = () => {
		const { maler } = this.props
		console.log('maler :', maler)
		return maler.map(mal => {
			return { value: mal.malBestillingNavn, label: mal.malBestillingNavn }
		})
	}
}

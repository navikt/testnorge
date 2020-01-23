import React, { Component, Fragment } from 'react'
import { Field } from 'formik'
import AttributtVelgerConnector from '~/components/attributtVelger/AttributtVelgerConnector'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import { getAttributesFromMal, getValuesFromMal } from './MalbestillingUtils'

export default class NyIdent extends Component {
	componentDidMount() {
		this.props.getBestillingMaler()
		this.props.maler.length > 0 && this._formatMalerOptions()
	}

	componentDidUpdate(prevProps) {
		const { mal } = this.props

		if (mal !== prevProps.mal) {
			if (!mal) {
				this.props.setBestillingFraMal({
					antallIdenter: 1,
					identtype: '',
					attributeIds: [],
					environments: [],
					values: {},
					currentMal: ''
				})
			} else {
				this.props.setBestillingFraMal({
					antallIdenter: mal.antallIdenter,
					identtype: mal.tpsfKriterier.identtype || 'FNR',
					attributeIds: getAttributesFromMal(mal),
					environments: mal.environments,
					values: getValuesFromMal(mal),
					currentMal: mal.malBestillingNavn
				})
			}
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
			antall,
			identtype
		} = this.props

		return (
			<Fragment>
				<div className="flexbox">
					<Field
						name="identtype"
						label="Velg identtype"
						className="input-field"
						component={FormikDollySelect}
						options={SelectOptionsManager('identtype')}
					/>
					<Field
						name="antall"
						label="Antall personer"
						className="input-field"
						type="number"
						min="0"
						component={FormikInput}
					/>
					<Field
						name="mal"
						label="MALER"
						className="input-field-mal-select"
						size="small"
						component={FormikDollySelect}
						placeholder={maler.length > 0 ? 'Mal ikke valgt' : 'Ingen maler'}
						options={this._formatMalerOptions(maler)}
					/>
				</div>
				<AttributtVelgerConnector
					onToggle={toggleAttributeSelection}
					uncheckAllAttributes={uncheckAllAttributes}
					checkAttributeArray={checkAttributeArray}
					uncheckAttributeArray={uncheckAttributeArray}
					selectedIds={selectedAttributeIds}
				/>
			</Fragment>
		)
	}

	_formatMalerOptions = () => {
		const { maler } = this.props
		return maler.map(mal => {
			return { value: mal.malBestillingNavn, label: mal.malBestillingNavn }
		})
	}

	_showMalSpecs = (maler, mal) => {
		const res = maler.find(m => m.malBestillingNavn === mal)

		return (
			<div>
				<p>{res.tpsfKriterier}</p>
				<p>{res.bestKriterier}</p>
			</div>
		)
	}
}

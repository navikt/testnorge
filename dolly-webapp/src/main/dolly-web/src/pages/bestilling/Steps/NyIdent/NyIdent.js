import React, { Component, Fragment } from 'react'
import '~/pages/bestilling/Bestilling.less'
import AttributtVelgerConnector from '~/components/attributtVelger/AttributtVelgerConnector'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { Field, withFormik } from 'formik'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import ContentTooltip from '~/components/contentTooltip/ContentTooltip'
import { getAttributesFromMal, getValuesFromMal } from './MalbestillingUtils'

export default class NyIdent extends Component {
	constructor(props) {
		super(props)
	}

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
					identtype: JSON.parse(mal.tpsfKriterier).identtype || 'FNR',
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
						// value={identtype || 'FNR'}
					/>
					<Field
						name="antall"
						label="Antall personer"
						className="input-field"
						type="number"
						min="0"
						component={FormikInput}
						// value={antall || 1}
					/>
					<Field
						name="mal"
						label="MALER"
						className="input-field"
						component={FormikDollySelect}
						placeholder={maler.length > 0 ? 'Mal ikke valgt' : 'Ingen maler'}
						options={this._formatMalerOptions(maler)}
					/>
					{/* <ContentTooltip>
						<span>
							Fødselsnummer er et ellevesifret registreringsnummer som tildeles av den norske stat
							til alle landets innbyggere. Nummeret skiller enkeltpersoner fra hverandre, men kan
							ikke brukes til å autentisere at en person er den de påstår de er. Fødselsnummer ble
							innført i 1964 og administreres av Skatteetaten. Alle som er bosatt i Norge og innført
							i Det sentrale folkeregisteret har enten et fødselsnummer eller et D-nummer.
						</span>
						<br />
						<a
							style={{ color: 'lightblue' }}
							href="https://no.wikipedia.org/wiki/F%C3%B8dselsnummer"
						>
							Les mer
						</a>
					</ContentTooltip> */}
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

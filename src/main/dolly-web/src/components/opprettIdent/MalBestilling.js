import React, { Component, Fragment } from 'react'
import '~/pages/bestilling/Bestilling.less'
import AttributtVelgerConnector from '~/components/attributtVelger/AttributtVelgerConnector'
import { FormikDollySelect } from '~/components/fields/Select/Select'
import { FormikInput } from '~/components/fields/Input/Input'
import { Field, withFormik } from 'formik'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import ContentTooltip from '~/components/contentTooltip/ContentTooltip'

export default class NyIdent extends Component {
	constructor(props) {
		super(props)
	}

	render() {
		const {
			selectedAttributeIds,
			toggleAttributeSelection,
			uncheckAllAttributes,
			checkAttributeArray,
			uncheckAttributeArray
		} = this.props

		return (
			<Fragment>
				<div className="flexbox">
					<Field
						name="identtype"
						label="Velg identtype"
						component={FormikDollySelect}
						options={SelectOptionsManager('identtype')}
					/>
					<Field
						name="antall"
						label="Antall personer"
						className="input-num-person"
						type="number"
						min="0"
						component={FormikInput}
					/>
					<ContentTooltip>
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
					</ContentTooltip>
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
}

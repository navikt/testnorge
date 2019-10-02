import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Stegindikator from 'nav-frontend-stegindikator'
import Steg1 from './Steps/Step1'
import Steg2 from './Steps/Step2'
import Steg3 from './Steps/Step3'
import { isPage } from './Utils'

import './Bestilling.less'

export default class Bestilling extends PureComponent {
	static propTypes = {}

	componentDidMount() {
		this.props.getEnvironments()
	}

	lagIdentListe = () => {
		let identListe = this.props.eksisterendeIdentListe.join(', ')
		return identListe
	}

	render() {
		const {
			values,
			page,
			identtype,
			antall,
			attributeIds,
			startBestilling,
			toggleAttribute,
			uncheckAllAttributes,
			checkAttributeArray,
			uncheckAttributeArray,
			setValues,
			deleteValues,
			deleteValuesArray,
			setEnvironments,
			sendBestilling,
			environments,
			eksisterendeIdentListe,
			identOpprettesFra,
			setIdentOpprettesFra,
			createBestillingMal,
			maler,
			currentMal
		} = this.props

		// Stygg workaround fordi  attributeIds ikke blir satt for mal med UDI tredjelandsborger uten oppholdstillatelse.
		// Tips mottas med takk!
		if (this.props.values.oppholdStatus) {
			if (this.props.values.oppholdStatus[0].tredjelandsBorgereValg === 'ikkeOppholdSammeVilkaar') {
				attributeIds.push('oppholdStatus')
			}
		}

		return (
			<div className="bestilling-page">
				<Stegindikator
					aktivtSteg={page}
					steg={[
						{ label: 'Velg egenskaper' },
						{ label: 'Velg verdier' },
						{ label: 'Oppsummering' }
					]}
					visLabel={true}
					kompakt={true}
				/>

				{isPage.first(page) && (
					<Steg1
						identtype={identtype}
						antall={antall}
						currentMal={currentMal}
						selectedAttributeIds={attributeIds}
						startBestilling={startBestilling}
						toggleAttributeSelection={toggleAttribute}
						uncheckAllAttributes={uncheckAllAttributes}
						checkAttributeArray={checkAttributeArray}
						uncheckAttributeArray={uncheckAttributeArray}
						eksisterendeIdentListe={eksisterendeIdentListe}
						identOpprettesFra={identOpprettesFra}
						setIdentOpprettesFra={setIdentOpprettesFra}
					/>
				)}

				{isPage.second(page) && (
					<Steg2
						identtype={identtype}
						antall={antall}
						selectedAttributeIds={attributeIds}
						setValues={setValues}
						values={values}
						identOpprettesFra={identOpprettesFra}
						eksisterendeIdentListe={this.props.eksisterendeIdentListe.join(', ')}
					/>
				)}

				{isPage.last(page) && (
					<Steg3
						identtype={identtype}
						antall={antall}
						selectedAttributeIds={attributeIds}
						values={values}
						sendBestilling={sendBestilling}
						setEnvironments={setEnvironments}
						createBestillingMal={createBestillingMal}
						environments={environments}
						deleteValues={deleteValues}
						deleteValuesArray={deleteValuesArray}
						identOpprettesFra={identOpprettesFra}
						eksisterendeIdentListe={this.props.eksisterendeIdentListe.join(', ')}
						maler={maler}
					/>
				)}
			</div>
		)
	}
}

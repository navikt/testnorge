import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import ProgressIndicator from './ProgressIndicator'
import Steg1 from './Steps/Step1'
import Steg2 from './Steps/Step2'
import Steg3 from './Steps/Step3'
import { isPage } from './Utils'

import './Bestilling.less'

export default class Bestilling extends PureComponent {
	static propTypes = {}

	render() {
		const {
			values,
			page,
			identtype,
			antall,
			attributeIds,
			startBestilling,
			toggleAttribute,
			setValues
		} = this.props

		return (
			<div className="bestilling-page">
				<ProgressIndicator activeStep={page} />

				{isPage.first(page) && (
					<Steg1
						startBestilling={startBestilling}
						selectedAttributeIds={attributeIds}
						toggleAttributeSelection={toggleAttribute}
						identtype={identtype}
						antall={antall}
					/>
				)}

				{isPage.second(page) && (
					<Steg2
						selectedAttributeIds={attributeIds}
						identtype={identtype}
						antall={antall}
						setValues={setValues}
					/>
				)}

				{isPage.last(page) && (
					<Steg3
						selectedAttributeIds={attributeIds}
						identtype={identtype}
						antall={antall}
						values={values}
					/>
				)}
			</div>
		)
	}
}

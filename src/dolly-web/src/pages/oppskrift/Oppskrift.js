import React, { Component } from 'react'
import PropTypes from 'prop-types'
import StepIndicator from './Steps/StepIndicator'
import OppskriftSteg1 from './Steps/OppskriftStep1'
import OppskriftSteg2 from './Steps/OppskriftStep2'
import OppskriftSteg3 from './Steps/OppskriftStep3'

import './Oppskrift.less'

export default class Oppskrift extends Component {
	static propTypes = {}

	state = {
		activeStep: 0
	}

	render() {
		const { activeStep } = this.state

		return (
			<div className="oppskrift-page">
				<StepIndicator activeStep={activeStep} />

				{activeStep === 0 && <OppskriftSteg1 />}
				{activeStep === 1 && <OppskriftSteg2 />}
				{activeStep === 2 && <OppskriftSteg3 />}

				{activeStep !== 2 && (
					<button onClick={() => this.setState({ activeStep: activeStep + 1 })}>Next</button>
				)}
			</div>
		)
	}
}

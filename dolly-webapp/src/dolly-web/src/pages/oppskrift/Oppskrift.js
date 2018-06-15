import React, { Component } from 'react'
import PropTypes from 'prop-types'
import StepIndicator from '~/pages/oppskrift/StepIndicator'
import OppskriftSteg1 from './OppskriftStep1'
import OppskriftSteg2 from './OppskriftStep2'
import OppskriftSteg3 from './OppskriftStep3'

export default class Oppskrift extends Component {
	static propTypes = {}

	state = {
		activeStep: 0
	}

	render() {
		const { activeStep } = this.state

		return (
			<div style={{ marginTop: '30px' }}>
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

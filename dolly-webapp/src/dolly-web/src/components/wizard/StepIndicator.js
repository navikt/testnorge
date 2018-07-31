import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Steps from 'rc-steps'

import 'rc-steps/assets/index.css'

export default class StepIndicator extends PureComponent {
	static propTypes = {
		activeStep: PropTypes.number.isRequired
	}

	render() {
		const { activeStep } = this.props

		return (
			<Steps labelPlacement="vertical" current={activeStep}>
				<Steps.Step title="Velg egenskaper" />
				<Steps.Step title="Velg verdier" />
				<Steps.Step title="Oppsummering" />
			</Steps>
		)
	}
}

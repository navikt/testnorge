import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Steps from 'rc-steps'

import 'rc-steps/assets/index.css'

export default class ProgressIndicator extends PureComponent {
	static propTypes = {
		activeStep: PropTypes.number,
		labelPlacement: PropTypes.string,
		steps: PropTypes.arrayOf(PropTypes.string)
	}

	static defaultProps = {
		activeStep: 1,
		labelPlacement: 'vertical',
		steps: []
	}

	render() {
		const { activeStep, labelPlacement, steps } = this.props

		// Zero indexed
		const currentStep = activeStep - 1

		return (
			<Steps labelPlacement={labelPlacement} current={currentStep}>
				{steps.map(stepTitle => <Steps.Step key={stepTitle} title={stepTitle} />)}
			</Steps>
		)
	}
}

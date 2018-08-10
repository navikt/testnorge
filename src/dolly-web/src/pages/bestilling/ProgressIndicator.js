import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Steps from 'rc-steps'

import 'rc-steps/assets/index.css'

export default class ProgressIndicator extends PureComponent {
	static propTypes = {
		activeStep: PropTypes.oneOf([1, 2, 3])
	}

	render() {
		const { activeStep } = this.props

		return (
			<Steps labelPlacement="vertical" current={activeStep - 1}>
				<Steps.Step title="Velg egenskaper" />
				<Steps.Step title="Velg verdier" />
				<Steps.Step title="Oppsummering" />
			</Steps>
		)
	}
}

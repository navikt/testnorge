import React, { Component } from 'react'
import StaticValue from '../StaticValue/StaticValue'
import Loading from '~/components/loading/Loading'

class KodeverkValue extends Component {
	componentDidMount() {
		const { fetchKodeverk } = this.props
		fetchKodeverk()
	}

	render() {
		const { kodeverkObject, value, extraLabel, showValue, ...restProps } = this.props
		if (!kodeverkObject) {
			return (
				<div className="static-value">
					<Loading onlySpinner />
				</div>
			)
		}
		let label = extraLabel ? extraLabel + ' - ' + kodeverkObject.label : kodeverkObject.label

		if (showValue) {
			label = kodeverkObject.value + ' - ' + label
		}

		return <StaticValue value={label} {...restProps} />
	}
}

export default KodeverkValue

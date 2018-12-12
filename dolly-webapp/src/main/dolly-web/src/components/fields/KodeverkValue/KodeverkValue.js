import React, { Component } from 'react'
import StaticValue from '../StaticValue/StaticValue'
import Loading from '~/components/loading/Loading'

class KodeverkValue extends Component {
	componentDidMount() {
		const { fetchKodeverk } = this.props
		fetchKodeverk()
	}

	render() {
		const { kodeverkObject, value, ...restProps } = this.props
		if (!kodeverkObject) {
			return (
				<div className="static-value">
					<Loading onlySpinner />
				</div>
			)
		}
		return <StaticValue value={kodeverkObject.label} {...restProps} />
	}
}

export default KodeverkValue

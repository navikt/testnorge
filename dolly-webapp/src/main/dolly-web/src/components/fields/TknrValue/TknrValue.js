import React, { Component } from 'react'
import StaticValue from '../StaticValue/StaticValue'
import Loading from '~/components/loading/Loading'

class TknrValue extends Component {
	componentDidMount() {
		const { fetchTknr } = this.props
		fetchTknr()
	}

	render() {
		const { tknrObject, value, ...restProps } = this.props
		if (!tknrObject) {
			return (
				<div className="static-value">
					<Loading onlySpinner />
				</div>
			)
		}
		return <StaticValue value={tknrObject} {...restProps} />
	}
}

export default TknrValue

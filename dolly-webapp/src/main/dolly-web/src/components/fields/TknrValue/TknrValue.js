import React, { Component } from 'react'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Loading from '~/components/loading/Loading'

export default class TknrValue extends Component {
	componentDidMount() {
		this.props.fetchTknr()
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

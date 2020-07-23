import * as React from 'react'
import './Loading.less'
import Spinner from './Spinner'

interface Loading {
	label?: string
	onlySpinner?: boolean
	size?: number
	fullpage?: boolean
	panel?: boolean
	className?: string
}

export default ({ onlySpinner, size, className, label, panel, fullpage }: Loading) => {
	if (onlySpinner) {
		return <Spinner size={size || 18} />
	}
	const Loader = () => (
		<div className={className ? className : 'loading-component'}>
			{label || 'Laster'}
			<Spinner size={size || 18} />
		</div>
	)
	if (panel) {
		return (
			<div className="panel-loading-container">
				<Loader />
			</div>
		)
	}
	if (fullpage) {
		return (
			<div className="fullpage-loading-container">
				<Loader />
			</div>
		)
	}
	return <Loader />
}

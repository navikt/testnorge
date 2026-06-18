import * as React from 'react'
import './Loading.less'
import Spinner from './Spinner'
import DollySpinner from './DollySpinner'

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
	if (fullpage) {
		return (
			<div className="fullpage-loading-container">
				<DollySpinner size={size || 160} label={label || 'Laster'} />
			</div>
		)
	}
	if (panel) {
		return (
			<div className="panel-loading-container">
				<DollySpinner size={size || 120} label={label || 'Laster'} />
			</div>
		)
	}
	if (label) {
		return (
			<div className={className ? className : 'panel-loading-container'}>
				<DollySpinner size={size || 80} label={label} />
			</div>
		)
	}
	return (
		<div className={className ? className : 'loading-component'}>
			<Spinner size={size || 18} />
		</div>
	)
}

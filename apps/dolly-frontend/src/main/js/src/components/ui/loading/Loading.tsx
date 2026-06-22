import * as React from 'react'
import './Loading.less'
import Spinner from './Spinner'
import DollySpinner from './DollySpinner'
import { Loader } from '@navikt/ds-react'

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
				<DollySpinner size={size || 120} label={label || 'Laster'} />
			</div>
		)
	}
	if (panel) {
		return (
			<div className="panel-loading-container">
				<DollySpinner size={size || 100} label={label || 'Laster'} />
			</div>
		)
	}
	if (label) {
		return (
			<div className={className ? className : 'inline-loading-container'}>
				<Loader size="small" title={label} />
				<span className="inline-loading-label">{label}</span>
			</div>
		)
	}
	return (
		<div className={className ? className : 'loading-component'}>
			<Spinner size={size || 18} />
		</div>
	)
}

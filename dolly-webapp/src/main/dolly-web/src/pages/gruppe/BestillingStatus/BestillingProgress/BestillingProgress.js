import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import { Line } from 'rc-progress'
import Loading from '~/components/loading/Loading'
import './BestillingProgress.less'

export default class BestillingProgress extends PureComponent {
	static propTypes = {
		status: PropTypes.object.isRequired
	}

	render() {
		const { status } = this.props

		return (
			<Fragment>
				<div className="flexbox--space">
					<h5>
						<Loading onlySpinner />
						{status.title}
					</h5>
					<span>{status.text}</span>
				</div>
				<div className="rc-progress-wrapper">
					<Line percent={status.percent} strokeWidth={0.5} trailWidth={0.5} strokeColor="#254b6d" />
				</div>
			</Fragment>
		)
	}
}

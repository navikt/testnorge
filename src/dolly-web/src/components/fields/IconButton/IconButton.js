import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'

import './IconButton.less'

export default class IconButton extends PureComponent {
	static propTypes = {
		iconName: PropTypes.string.isRequired,
		onClick: PropTypes.func.isRequired
	}

	render() {
		const { onClick, iconName } = this.props

		const cssClass = cn('fa', `fa-${iconName}`)

		return (
			<button className="iconbutton" onClick={onClick}>
				<i className={cssClass} />
			</button>
		)
	}
}

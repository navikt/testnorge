import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'

import './IconButton.less'

export default class IconButton extends PureComponent {
	static propTypes = {
		iconName: PropTypes.string.isRequired,
		onClick: PropTypes.func.isRequired
	}

	onClickHandler = event => {
		event.stopPropagation()
		return this.props.onClick()
	}

	render() {
		const { iconName } = this.props

		const cssClass = cn('fa', `fa-${iconName}`)

		return (
			<button className="iconbutton" onClick={this.onClickHandler}>
				<i className={cssClass} />
			</button>
		)
	}
}

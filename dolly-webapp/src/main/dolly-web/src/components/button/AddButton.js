import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Icon from '~/components/icon/Icon'

export default class AddButton extends PureComponent {
	static propTypes = {
		title: PropTypes.string,
		onClick: PropTypes.func
	}

	onClickHandler = event => {
		event.stopPropagation()
		return this.props.onClick()
	}

	render() {
		return (
			<div className="big-add-wrapper">
				<button
					type="button"
					title={this.props.title}
					className="big-add-button"
					onClick={this.onClickHandler}
				>
					<Icon kind="add" size="30" />
				</button>
			</div>
		)
	}
}

import React, { PureComponent, Fragment } from 'react'
import Button from '~/components/button/Button'
import './RemovableField.less'

class RemovableField extends PureComponent {
	static defaultProps = {
		removable: true
	}

	constructor(props) {
		super(props)
		this.state = { onButtonHover: false }
	}

	render() {
		const { onRemove, removable } = this.props
		return (
			<div className="removable-column">
				<div className={this.state.onButtonHover ? 'removable-body-hover' : null}>
					{this.props.children}
				</div>
				{removable === true && (
					<div
						className='removable-button'
						onMouseEnter={() => this.setState({ onButtonHover: true })}
						onMouseLeave={() => this.setState({ onButtonHover: false })}
					>
						<Button
							className="field-group-remove"
							kind="remove-circle"
							onClick={() => onRemove(this.key)}
						/>
					</div>
				)}
			</div>
		)
	}
}

export default RemovableField

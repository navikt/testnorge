import React, { PureComponent } from 'react'
import Button from '~/components/button/Button'
import './RemovableField.less'

// TODO: Bruk classnames pakke

export default class RemovableField extends PureComponent {
	static defaultProps = {
		removable: true,
		removableText: null
	}

	state = { onButtonHover: false }

	render() {
		const { onRemove, removable, removableText } = this.props
		return (
			<div className={removableText == null ? 'removable-column' : 'removable-column-text'}>
				<div className={this.state.onButtonHover ? 'removable-body-hover' : 'removable-body'}>
					{this.props.children}
				</div>
				{removable && (
					<div className="removable-body-button">
						<div
							className="removable-button"
							onMouseEnter={() => this.setState({ onButtonHover: true })}
							onMouseLeave={() => this.setState({ onButtonHover: false })}
						>
							<Button
								className="field-group-remove"
								kind="remove-circle"
								onClick={() => onRemove(this.key)}
							/>
						</div>
						{removableText != null && <p>{removableText}</p>}
					</div>
				)}
			</div>
		)
	}
}

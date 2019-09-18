import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import Tooltip from 'rc-tooltip'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Icon from '~/components/ui/icon/Icon'
import Button from '~/components/ui/button/Button'
import Formatters from '~/utils/DataFormatter'

import './ContentTooltip.less'

export default class ContentTooltip extends PureComponent {
	static propTypes = {
		children: PropTypes.node.isRequired
	}

	state = {
		isOpen: false,
		tilgjengeligeMiljoe: ''
	}

	render() {
		const { children, hideText } = this.props

		return (
			<div className="flexbox--align-center content-tooltip" onClick={this._handleOnClick}>
				<div className="hjelpetekst">
					{this._renderHjelpeIkon()}
					{this.state.isOpen && this._renderHjelpeTekst(children)}
				</div>
				{!hideText && <p className="clickable-text-small">Info</p>}
			</div>
		)
	}

	_handleOnClick = async () => {
		this.setState({ isOpen: !this.state.isOpen })
		let res
		if (this.props.tilgjengeligeMiljoeEndepunkt) {
			try {
				res = await this.props.tilgjengeligeMiljoeEndepunkt
			} catch (err) {
				res = 'Fant ingen miljø'
				console.error(err)
			}
		}
		return this.setState({ tilgjengeligeMiljoe: res })
	}

	_renderHjelpeIkon = () => (
		<button
			type="button"
			className="hjelpetekst__apneknapp"
			title="Hjelptekst"
			aria-label="Hjelptekst"
			aria-pressed={this.state.isOpen ? 'true' : 'false'}
			aria-describedby={this.state.isOpen ? 'tooltip-hjelpetekst' : null}
		>
			<span className="sr-only">Hjelptekst</span>
			<Icon kind={this.state.isOpen ? 'help-circle-filled' : 'help-circle'} />
		</button>
	)

	_renderHjelpeTekst = children => (
		<div
			tabIndex="-1"
			id="tooltip-hjelpetekst"
			role="tooltip"
			className="hjelpetekst__tooltip content hjelpetekst__tooltip--under"
		>
			<div className="hjelpetekst__tekst">
				<p className="typo-normal">{children}</p>
				{this.state.tilgjengeligeMiljoe && (
					<p className="typo-normal">
						Tilgjengelige miljø: {Formatters.arrayToString(this.state.tilgjengeligeMiljoe.data)}
					</p>
				)}
			</div>
			<button
				className="lukknapp lukknapp--hvit"
				aria-controls="tooltip-hjelpetekst"
				aria-label="Lukk hjelpetekst"
				onClick={this._handleOnClick}
			>
				Lukk hjelpetekst
			</button>
		</div>
	)
}

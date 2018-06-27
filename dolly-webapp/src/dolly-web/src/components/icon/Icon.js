import React, { Component } from 'react'
import PropTypes from 'prop-types'

const iconList = ['advarsel-sirkel']

export default class Icon extends Component {
	static propTypes = {
		height: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		width: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		kind: PropTypes.oneOf(iconList).isRequired,
		size: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		style: PropTypes.oneOfType([PropTypes.array, PropTypes.object])
	}

	static defaultProps = {
		size: 32
	}

	render() {
		const { kind } = this.props

		return this.renderIcon(kind)
	}

	getIcon(kind) {
		const { height, onClick, size, style, width, ...props } = this.props

		// prettier-ignore
		switch (kind) {
            default: return null;
            case ('trashcan'): return (<svg {...props} focusable="false" height={height || size} width={width || size} onClick={onClick} style={style} viewBox="0 0 24 24"><title>Søppelkasse</title><path d="M3.516 3.5h16v20h-16zm4-3h8v3h-8zm-6.5 3h22M7.516 7v12m4-12v12m4-12v12" stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"/></svg>);
      }
	}
}

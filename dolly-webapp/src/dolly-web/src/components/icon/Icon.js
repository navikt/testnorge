import React, { Component } from 'react'
import PropTypes from 'prop-types'

const iconList = [
	'trashcan',
	'add-circle',
	'remove-circle',
	'edit',
	'star',
	'star-filled',
	'user',
	'search'
]

export default class Icon extends Component {
	static propTypes = {
		height: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		width: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		kind: PropTypes.oneOf(iconList).isRequired,
		size: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		style: PropTypes.oneOfType([PropTypes.array, PropTypes.object])
	}

	static defaultProps = {
		size: 24
	}

	render() {
		const { kind } = this.props

		return this.getIcon(kind)
	}

	getIcon(kind) {
		const { height, size, style, width, ...props } = this.props

		// prettier-ignore
		switch (kind) {
            default: return null
            case ('trashcan'): return (<svg {...props} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Søppelkasse</title><path d="M3.516 3.5h16v20h-16zm4-3h8v3h-8zm-6.5 3h22M7.516 7v12m4-12v12m4-12v12" stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"/></svg>)
            case ('add-circle'): return (<svg {...props} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Legg til</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><circle cx="11.5" cy="11.5" r="11" /><path d="M11.5 5.5v12M17.5 11.5h-12" /></g></svg>)
            case ('remove-circle'): return (<svg {...props} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Fjern</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><circle cx="11.5" cy="11.5" r="11" /><path d="M15.7,7.3l-8.5,8.5 M15.7,15.7L7.3,7.3"/></g></svg>)
            case ('edit'): return (<svg {...props} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Rediger</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><path d="M7.31 21.675l-6.466 1.517 1.517-6.465 15.6-15.602c.781-.781 2.049-.781 2.829 0l2.122 2.122c.78.781.78 2.046 0 2.829l-15.602 15.599zM22.207 6.784l-4.954-4.952M20.78 8.211l-4.941-4.965M7.562 21.425l-4.95-4.951"/></g></svg>)
            case ('star'): return (<svg {...props} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Favoritt</title><path stroke="#000" strokeLinejoin="round" strokeMiterlimit="10" fill="none" d="M12 .5l3 8.5h8.5l-7 5.5 3 9-7.5-5.5-7.5 5.5 3-9-7-5.5h8.5z"/></svg>)
            case ('star-filled'): return (<svg {...props} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Favoritt</title><path d="M23.973,8.836C23.902,8.635,23.713,8.5,23.5,8.5h-8.146l-2.883-8.166C12.4,0.134,12.211,0,12,0c-0.213,0-0.401,0.134-0.472,0.334L8.646,8.5H0.5c-0.213,0-0.403,0.135-0.473,0.336c-0.071,0.201-0.004,0.426,0.164,0.557l6.723,5.283l-2.889,8.666c-0.069,0.207,0.004,0.435,0.18,0.563s0.414,0.128,0.59-0.001L12,18.62l7.204,5.283C19.292,23.969,19.396,24,19.5,24s0.206-0.031,0.294-0.096c0.177-0.128,0.249-0.355,0.181-0.563l-2.89-8.666l6.724-5.283C23.977,9.262,24.042,9.037,23.973,8.836z"/></svg>)
            case ('user'): return (<svg {...props} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Bruker</title><path d="M12,0C5.383,0,0,5.383,0,12c0,3.18,1.232,6.177,3.469,8.438l0,0.001C5.743,22.735,8.772,24,12,24c3.234,0,6.268-1.27,8.542-3.573C22.772,18.166,24,15.174,24,12C24,5.383,18.617,0,12,0z M20.095,19.428c-1.055-0.626-2.64-1.202-4.32-1.81c-0.418-0.151-0.846-0.307-1.275-0.465v-1.848c0.501-0.309,1.384-1.107,1.49-2.935c0.386-0.227,0.63-0.728,0.63-1.37c0-0.578-0.197-1.043-0.52-1.294c0.242-0.757,0.681-2.145,0.385-3.327C16.138,4.992,14.256,4.5,12.75,4.5c-1.342,0-2.982,0.391-3.569,1.456C8.477,5.922,8.085,6.229,7.891,6.487c-0.635,0.838-0.216,2.368,0.021,3.21C7.583,9.946,7.38,10.415,7.38,11c0,0.643,0.244,1.144,0.63,1.37c0.106,1.828,0.989,2.626,1.49,2.935v1.848c-0.385,0.144-0.78,0.287-1.176,0.431c-1.621,0.587-3.288,1.194-4.407,1.857C2.04,17.405,1,14.782,1,12C1,5.935,5.935,1,12,1c6.065,0,11,4.935,11,11C23,14.775,21.965,17.394,20.095,19.428z"/></svg>)
            case ('search'): return (<svg {...props} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Søk</title><g stroke="#000" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><circle cx="8.5" cy="8.5" r="8"/><path strokeLinecap="round" d="M14.156 14.156l9.344 9.344"/></g></svg>)
        }
	}
}

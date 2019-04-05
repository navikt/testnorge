import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'

import './Icon.less'

export const iconList = [
	'trashcan',
	'add-circle',
	'remove-circle',
	'add',
	'edit',
	'eraser',
	'star',
	'star-filled',
	'user',
	'search',
	'chevron-up',
	'chevron-down',
	'chevron-left',
	'chevron-right',
	'file-new',
	'file-new-table',
	'team',
	'info-circle',
	'report-problem-circle',
	'report-problem-triangle',
	'search',
	'arrow-up',
	'arrow-down',
	'arrow-circle-right',
	'arrow-circle-left',
	'feedback-check-circle',
	'arrow-left',
	'arrow-right',
	'synchronize',
	'details'
]

export default class Icon extends PureComponent {
	static propTypes = {
		height: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		width: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		kind: PropTypes.oneOf(iconList),
		size: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		style: PropTypes.oneOfType([PropTypes.array, PropTypes.object])
	}

	static defaultProps = {
		size: 24
	}

	render() {
		const { kind } = this.props
		if (!kind) return null
		return this.getIcon(kind)
	}

	getIcon(kind) {
		const { height, size, style, width, className, ...props } = this.props

		// prettier-ignore
		switch (kind) {
            default: return null
            case ('trashcan'): return (<svg {...props} className={cn('svg-icon-trashcan', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Søppelkasse</title><path d="M3.516 3.5h16v20h-16zm4-3h8v3h-8zm-6.5 3h22M7.516 7v12m4-12v12m4-12v12" stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"/></svg>)
            case ('add-circle'): return (<svg {...props} className={cn('svg-icon-add-circle', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Legg til</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><circle cx="11.5" cy="11.5" r="11" /><path d="M11.5 5.5v12M17.5 11.5h-12" /></g></svg>)
            case ('remove-circle'): return (<svg {...props} className={cn('svg-icon-remove-circle', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Fjern</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><circle cx="11.5" cy="11.5" r="11" /><path d="M15.7,7.3l-8.5,8.5 M15.7,15.7L7.3,7.3"/></g></svg>)
            case ('add'): return (<svg {...props} className={cn('svg-icon-add', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Legg til</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><path d="M11.5 5.5v12M17.5 11.5h-12" /></g></svg>)
            case ('edit'): return (<svg {...props} className={cn('svg-icon-edit', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Rediger</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><path d="M7.31 21.675l-6.466 1.517 1.517-6.465 15.6-15.602c.781-.781 2.049-.781 2.829 0l2.122 2.122c.78.781.78 2.046 0 2.829l-15.602 15.599zM22.207 6.784l-4.954-4.952M20.78 8.211l-4.941-4.965M7.562 21.425l-4.95-4.951"/></g></svg>)
            case ('star'): return (<svg {...props} className={cn('svg-icon-star', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Favoritt</title><path stroke="#000" strokeLinejoin="round" strokeMiterlimit="10" fill="none" d="M12 .5l3 8.5h8.5l-7 5.5 3 9-7.5-5.5-7.5 5.5 3-9-7-5.5h8.5z"/></svg>)
            case ('star-filled'): return (<svg {...props} className={cn('svg-icon-star-filled', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Favoritt</title><path d="M23.973,8.836C23.902,8.635,23.713,8.5,23.5,8.5h-8.146l-2.883-8.166C12.4,0.134,12.211,0,12,0c-0.213,0-0.401,0.134-0.472,0.334L8.646,8.5H0.5c-0.213,0-0.403,0.135-0.473,0.336c-0.071,0.201-0.004,0.426,0.164,0.557l6.723,5.283l-2.889,8.666c-0.069,0.207,0.004,0.435,0.18,0.563s0.414,0.128,0.59-0.001L12,18.62l7.204,5.283C19.292,23.969,19.396,24,19.5,24s0.206-0.031,0.294-0.096c0.177-0.128,0.249-0.355,0.181-0.563l-2.89-8.666l6.724-5.283C23.977,9.262,24.042,9.037,23.973,8.836z"/></svg>)
            case ('user'): return (<svg {...props} className={cn('svg-icon-user', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Bruker</title><path d="M12,0C5.383,0,0,5.383,0,12c0,3.18,1.232,6.177,3.469,8.438l0,0.001C5.743,22.735,8.772,24,12,24c3.234,0,6.268-1.27,8.542-3.573C22.772,18.166,24,15.174,24,12C24,5.383,18.617,0,12,0z M20.095,19.428c-1.055-0.626-2.64-1.202-4.32-1.81c-0.418-0.151-0.846-0.307-1.275-0.465v-1.848c0.501-0.309,1.384-1.107,1.49-2.935c0.386-0.227,0.63-0.728,0.63-1.37c0-0.578-0.197-1.043-0.52-1.294c0.242-0.757,0.681-2.145,0.385-3.327C16.138,4.992,14.256,4.5,12.75,4.5c-1.342,0-2.982,0.391-3.569,1.456C8.477,5.922,8.085,6.229,7.891,6.487c-0.635,0.838-0.216,2.368,0.021,3.21C7.583,9.946,7.38,10.415,7.38,11c0,0.643,0.244,1.144,0.63,1.37c0.106,1.828,0.989,2.626,1.49,2.935v1.848c-0.385,0.144-0.78,0.287-1.176,0.431c-1.621,0.587-3.288,1.194-4.407,1.857C2.04,17.405,1,14.782,1,12C1,5.935,5.935,1,12,1c6.065,0,11,4.935,11,11C23,14.775,21.965,17.394,20.095,19.428z"/></svg>)
            case ('search'): return (<svg {...props} className={cn('svg-icon-search', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Søk</title><g stroke="#000" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><circle cx="8.5" cy="8.5" r="8"/><path strokeLinecap="round" d="M14.156 14.156l9.344 9.344"/></g></svg>)
            case ('chevron-down'): return (<svg {...props} className={cn('svg-icon-chevron-down', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Pil ned</title><Chevron direction="down" /></svg>)
            case ('chevron-up'): return (<svg {...props} className={cn('svg-icon-chevron-up', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Pil opp</title><Chevron direction="up" /></svg>)
            case ('chevron-left'): return (<svg {...props} className={cn('svg-icon-chevron-left', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Pil venstre</title><Chevron direction="left" /></svg>)
            case ('chevron-right'): return (<svg {...props} className={cn('svg-icon-chevron-right', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Pil høyre</title><Chevron direction="right" /></svg>)
            case ('file-new'): return (<svg {...props} className={cn('svg-icon-file-new', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Maler</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><path d="M20.5 23.5h-17v-23h11l6 6zM14.5.5v6h6"/></g></svg>)
            case ('team'): return (<svg {...props} className={cn('svg-icon-team', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Team</title><g stroke="#000" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><path d="M4 6.609v1.391l-2.539.726c-.528.151-.961.724-.961 1.274v.5h5.5M7 6.578v1.422l2.538.726c.529.151.962.724.962 1.274v.5h-5.5"/><ellipse cx="5.5" cy="3.771" rx="3" ry="3.271"/><path d="M8.469 3.198c-.5.5-1.93.476-2.469-.527-1 1-2.625 1-3.434.429M17 6.609v1.391l-2.539.726c-.528.151-.961.724-.961 1.274v.5h5.5M20 6.594v1.406l2.538.726c.529.151.962.724.962 1.274v.5h-5.5"/><ellipse cx="18.5" cy="3.771" rx="3" ry="3.271"/><path d="M21.453 3.195c-.5.5-1.914.479-2.453-.524-1 1-2.625 1-3.434.429M10.5 19.609v1.391l-2.539.726c-.528.151-.961.724-.961 1.274v.5h5.5M13.5 19.609v1.391l2.538.726c.529.151.962.724.962 1.274v.5h-5.5"/><ellipse cx="12" cy="16.771" rx="3" ry="3.271"/><path d="M14.953 16.171c-.5.5-1.914.503-2.453-.5-1 1-2.625 1-3.434.429"/><path strokeLinecap="round" d="M4 12.5l2.5 2.5M20 12.5l-2.5 2.5"/></g></svg>)
            case ('info-circle'): return (<svg {...props} className={cn('svg-icon-info-circle', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Info</title><g><circle stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" cx="11.5" cy="12.5" r="11" fill="none"/><path stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none" d="M8.5 19.5h6M9.5 10.5h2v8.5"/><path stroke="#000" strokeLinejoin="round" strokeMiterlimit="10" d="M11 6c-.277 0-.5.225-.5.5 0 .277.223.5.5.5.275 0 .5-.223.5-.5 0-.275-.225-.5-.5-.5z" fill="none"/></g></svg>)
            case ('arrow-left'): return (<svg {...props} className={cn('svg-icon-arrow-left', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Pil venstre</title><path fill="#3e3832" d="M21.746,0.064c-0.156-0.088-0.349-0.084-0.504,0.008l-19,11.5C2.091,11.663,2,11.825,2,12s0.091,0.337,0.241,0.428l19,11.5  C21.321,23.976,21.41,24,21.5,24c0.084,0,0.169-0.021,0.246-0.064C21.903,23.847,22,23.681,22,23.5v-23  C22,0.319,21.903,0.153,21.746,0.064z"/></svg>)
            case ('arrow-right'): return (<svg {...props} className={cn('svg-icon-arrow-right', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Pil høyre</title><path fill="#3e3832" d="M21.759,11.577L2.786,0.077C2.631-0.017,2.439-0.02,2.281,0.069C2.124,0.158,2.027,0.324,2.027,0.505v23  c0,0.181,0.097,0.347,0.254,0.436c0.077,0.043,0.161,0.064,0.246,0.064c0.09,0,0.18-0.024,0.259-0.072l18.973-11.5  C21.909,12.342,22,12.18,22,12.005S21.909,11.668,21.759,11.577z"/></svg>)
			case ('report-problem-circle'): return ( <svg {...props} className={cn('svg-icon-report-problem-circle', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"> <g fill="#258965"> <circle className="circle" cx="12" cy="12" r="8"/><path className="path" d="M11.696,0.996l-0.205,0.002c-3.08,0.054-5.979,1.299-8.162,3.506c-2.199,2.223-3.38,5.133-3.327,8.195
				C0.112,19.036,5.077,24,11.305,24l0.203-0.002c6.446-0.111,11.601-5.361,11.49-11.7C22.888,5.961,17.923,0.996,11.696,0.996z
					M11,6.996c0-0.276,0.224-0.5,0.5-0.5c0.275,0,0.5,0.224,0.5,0.5v7c0,0.276-0.225,0.5-0.5,0.5c-0.276,0-0.5-0.224-0.5-0.5V6.996z
					M11.518,18.496c-0.006,0-0.012,0-0.018,0c-0.545,0-0.991-0.436-1-0.982c-0.01-0.552,0.43-1.008,0.982-1.017
				c0.006,0,0.012-0.001,0.018-0.001c0.545,0,0.99,0.437,1,0.983C12.51,18.031,12.069,18.486,11.518,18.496z"/>
				</g></svg>)

			case ('report-problem-triangle'): return (
				<svg {...props} className={cn('svg-icon-report-problem-triangle', className)}
				focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24">	
	 			<path className="path" d="M22.906,23.196L11.947,1.276c-0.17-0.339-0.726-0.339-0.895,0l-11,22c-0.078,0.155-0.069,0.339,0.021,0.486S0.326,24,0.5,24
				h22c0.003,0,0.006,0,0.01,0c0.275,0,0.5-0.224,0.5-0.5C23.01,23.386,22.971,23.28,22.906,23.196z M11,9.152
				c0-0.276,0.224-0.5,0.5-0.5c0.275,0,0.5,0.224,0.5,0.5v7.653c0,0.275-0.225,0.5-0.5,0.5c-0.276,0-0.5-0.225-0.5-0.5V9.152z
				M11.5,21.108c-0.529,0-0.957-0.428-0.957-0.956c0-0.527,0.428-0.957,0.957-0.957c0.527,0,0.956,0.43,0.956,0.957
				C12.456,20.681,12.027,21.108,11.5,21.108z"/>
				</svg>)

			case ('search'): return (<svg {...props} className={cn('svg-icon-search', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Søk</title><g stroke="#000" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><circle cx="8.5" cy="8.5" r="8"/><path strokeLinecap="round" d="M14.156 14.156l9.344 9.344"/></g></svg>)
            case ('arrow-up'): return (<svg {...props} className={cn('svg-arrow-up', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Pil opp</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><path d="M6.513 5.5l5-5 5 5M11.513.5v23"/></g></svg>)
            case ('arrow-down'): return (<svg {...props} className={cn('svg-arrow-down', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Pil ned</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"><path d="M16.513 18.5l-5 5-5-5M11.513 23.5v-23"/></g></svg>)
			case ('arrow-circle-right'): return (<svg {...props} className={cn('svg-arrow-circle-right', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Arrow up</title>
	   			<path d="M12,0.004c-6.617,0-12,5.383-12,12s5.383,12,12,12s12-5.383,12-12S18.617,0.004,12,0.004z M17.317,12.401l-8.514,6.496
					C8.713,18.967,8.606,19,8.5,19c-0.151,0-0.3-0.067-0.398-0.196c-0.167-0.22-0.125-0.534,0.094-0.701l7.993-6.099L8.196,5.897
					C7.977,5.729,7.935,5.416,8.103,5.196c0.168-0.219,0.482-0.261,0.701-0.094l8.514,6.504c0.124,0.095,0.196,0.242,0.196,0.397
					C17.514,12.16,17.441,12.307,17.317,12.401z"/> </svg>)
			case ('arrow-circle-left'): return (<svg {...props} className={cn('svg-arrow-circle-left', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Arrow up</title>
				<path d="M12,0.004c-6.617,0-12,5.383-12,12s5.383,12,12,12s12-5.383,12-12S18.617,0.004,12,0.004z M15.897,18.804
					C15.799,18.933,15.65,19,15.5,19c-0.106,0-0.212-0.033-0.303-0.103l-8.5-6.496C6.573,12.307,6.5,12.16,6.5,12.004
					c0-0.155,0.072-0.303,0.196-0.397l8.5-6.504c0.219-0.167,0.534-0.125,0.701,0.094c0.168,0.219,0.126,0.533-0.093,0.701l-7.98,6.106
					l7.98,6.099C16.023,18.271,16.065,18.584,15.897,18.804z"/>
		  		</svg>)

		  	case ('feedback-check-circle'): return (<svg {...props} className={cn('svg-icon-feedback-check-circle', className)}
				focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24">
				<title>Checkmark-success</title>
				<g>
					<circle className="circle" cx="12" cy="12" r="11.5"/>
					<path className="checkmark" d="M17,8.5l-7.5,7L7,13"/>
				</g>
			</svg>)
			case ('synchronize'): return (<svg {...props} className={cn('svg-icon-synchronize', className)}
				focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24">
				<title>Gjenopprett</title>
				<g fill="#3e3832">
					<path fill="#3e3832"
						d="M23.866,14.227l-2.42-4.186c-0.041-0.069-0.086-0.134-0.141-0.19c-0.167-0.185-0.397-0.304-0.65-0.326   c-0.326-0.024-0.643,0.104-0.854,0.354l-3.213,3.832c-0.355,0.423-0.3,1.054,0.123,1.408c0.422,0.356,1.055,0.3,1.408-0.123   l1.456-1.735c-0.552,2.833-2.817,5.341-5.801,6.04c-2.529,0.595-5.143-0.146-6.995-1.976c-0.392-0.389-1.025-0.386-1.414,0.008   c-0.389,0.393-0.385,1.025,0.008,1.414c1.81,1.79,4.198,2.756,6.667,2.756c0.727,0,1.461-0.084,2.19-0.255   c3.605-0.845,6.377-3.801,7.213-7.212l0.689,1.192c0.186,0.321,0.522,0.5,0.866,0.5c0.17,0,0.343-0.043,0.5-0.135   C23.979,15.317,24.142,14.706,23.866,14.227z" />
					<path fill="#3e3832"
						d="M7.415,9.892c-0.424-0.354-1.054-0.3-1.408,0.124L4.284,12.07c0.033-1.282,0.396-2.549,1.085-3.697   c1.111-1.854,2.927-3.194,4.983-3.676c2.695-0.633,5.507,0.267,7.341,2.345c0.365,0.414,0.997,0.454,1.411,0.089   c0.414-0.365,0.454-0.997,0.089-1.412c-2.322-2.632-5.885-3.771-9.298-2.969C7.316,3.354,5.041,5.029,3.653,7.345   c-0.624,1.041-1.029,2.162-1.229,3.312L1.858,9.713c-0.285-0.473-0.9-0.626-1.373-0.342c-0.472,0.284-0.627,0.898-0.342,1.372   l2.561,4.262c0.166,0.277,0.456,0.457,0.778,0.482c0.026,0.002,0.051,0.003,0.078,0.003c0.294,0,0.575-0.13,0.767-0.357   l3.212-3.832C7.893,10.877,7.838,10.246,7.415,9.892z" />
				</g></svg>)
			case ('details'): return (<svg {...props} className={cn('svg-icon-details', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24">
				<title>Bestillingsdetaljer</title><g stroke="#0067C5" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none">
				</g>
				<g><line id="svg_5" y2="1.739583" x2="18.361613" y1="1.739583" x1="0.666667" strokeWidth="1"/>
					<line id="svg_6" y2="6.946506" x2="18.361613" y1="6.946506" x1="0.666667" strokeWidth="1"/>
					<line id="svg_10" y2="12" x2="9.194946" y1="12" x1="0.666667" strokeWidth="1"/>
					<line id="svg_4" y2="17" x2="9.194946" y1="17" x1="0.666667" strokeWidth="1"/>
					<ellipse ry="4.083333" rx="4.083333" id="svg_7" cy="14.822917" cx="15.75" strokeWidth="2" fill="none"/>
					<line id="svg_9" y2="22.239583" x2="22.333333" y1="17.90625" x1="18.666667" strokeWidth="2"/>
				 </g></svg>)
			case ('eraser'): return (<svg {...props} className={cn('svg-icon-eraser', className)}
				focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24">
				<title>Slett</title>
				<g><path d="M22.439,11.268c0.975-0.975,0.975-2.561,0-3.535l-5.172-5.172C16.797,2.09,16.169,1.831,15.5,1.831   c-0.67,0-1.297,0.259-1.768,0.729L9.5,6.793l8.707,8.707L22.439,11.268z" />
					<path fill="none" d="M3.732,20.439C4.607,21.314,6.262,22,7.5,22h2c1.237,0,2.893-0.686,3.768-1.561l4.232-4.232L8.793,7.5l-7.232,7.232   C1.09,15.203,0.83,15.831,0.83,16.5c0,0.67,0.26,1.297,0.73,1.768L3.732,20.439z" />
					<path d="M23.5,23h-23C0.224,23,0,23.224,0,23.5S0.224,24,0.5,24h23c0.276,0,0.5-0.224,0.5-0.5S23.776,23,23.5,23z" />
				</g></svg>)
			case ('file-new-table'): return (<svg {...props} className={cn('svg-icon-file-new-lines', className)} focusable="false" height={height || size} width={width || size} style={style} viewBox="0 0 24 24"><title>Ny fil med ruter</title><g stroke="#000" strokeLinecap="round" strokeLinejoin="round" strokeMiterlimit="10" fill="none"></g>
				<g><path d="M 20.961 6.308 c -0.025 -0.061 -0.062 -0.116 -0.108 -0.162 l -5.999 -5.999 c -0.046 -0.046 -0.101 -0.083 -0.162 -0.108 C 14.632 0.014 14.567 0 14.5 0 h -11 C 3.224 0 3 0.224 3 0.5 v 23 C 3 23.776 3.224 24 3.5 24 h 17 c 0.276 0 0.5 -0.224 0.5 -0.5 v -17 C 21 6.433 20.986 6.368 20.961 6.308 Z M 15 1.707 L 19.293 6 H 15 V 1.707 Z M 4 23 V 1 h 10 v 5.5 C 14 6.776 14.224 7 14.5 7 H 20 v 16 H 4 Z"/>
				<path d= "M 17.5 8 h -11 C 6.224 8 6 8.224 6 8.5 v 12 C 6 20.776 6.224 21 6.5 21 h 11 c 0.276 0 0.5 -0.224 0.5 -0.5 v -12 C 18 8.224 17.776 8 17.5 8 Z M 10 14 H 7 v -2 h 3 V 14 Z M 11 12 h 6 v 2 h -6 V 12 Z M 7 15 h 3 v 2 H 7 V 15 Z M 11 15 h 6 v 2 h -6 V 15 Z M 17 11 h -6 V 9 h 6 V 11 Z M 10 9 v 2 H 7 V 9 H 10 Z M 7 18 h 3 v 2 H 7 V 18 Z M 11 20 v -2 h 6 v 2 H 11 Z"/>
				</g></svg>)
		}
	}
}

export class Chevron extends PureComponent {
	static propTypes = {
		direction: PropTypes.oneOf(['up', 'down', 'left', 'right'])
	}

	static defaultProps = {
		direction: 'up'
	}

	render() {
		const transforms = {
			up: 'rotate(-90 12.109999656677246,11.699999809265138)',
			down: 'rotate(90 12.109999656677246,11.699999809265138)',
			left: 'rotate(-180 12.109999656677246,11.699999809265138)',
			right: 'rotate(0 12.109999656677246,11.699999809265138)'
		}

		return (
			<g transform={transforms[this.props.direction]}>
				<path d="m15.45545,11.74332l-8.69328,-9.03293a0.84851,0.84851 0 0 1 0,-1.19181a0.82782,0.82782 0 0 1 1.17964,0l9.53205,9.62701a0.84851,0.84851 0 0 1 0,1.19181l-9.53205,9.62701a0.82782,0.82782 0 0 1 -1.17964,0a0.84851,0.84851 0 0 1 0,-1.19181l8.69328,-9.02928z" />
			</g>
		)
	}
}

import React from 'react'
import SVG from 'react-inlinesvg'
import cn from 'classnames'

import Wrench from '@/assets/icons/nav-ikoner/line/SVG/21-settings/wrench.svg'
import ProblemTriangle from '@/assets/icons/custom/ProblemTriangle.svg'
import ReportProblemCircle from '@/assets/icons/custom/ProblemCircle.svg'
import CheckCircle from '@/assets/icons/custom/CheckCircle.svg'

import './Icon.less'

export const icons = {
	'feedback-check-circle': CheckCircle,
	'report-problem-circle': ReportProblemCircle,
	'report-problem-triangle': ProblemTriangle,
	arbeid: Wrench,
}

const px = (v: number) => `${v}px`

const Icon = ({
	kind = null,
	title = undefined,
	size = 24,
	style = undefined,
	className = undefined,
}) => {
	// @ts-ignore
	if (!icons?.[kind]) {
		return null
	}

	const cssClass = cn('svg-icon', `svg-icon-${kind}`, className)
	const styleObj = Object.assign({ width: px(size), height: px(size) }, style)

	return (
		// @ts-ignore
		<SVG src={icons[kind]} className={cssClass} style={styleObj} title={title} role={'img'}>
			<img
				src="../assets/icons/nav-ikoner/filled/SVG/01-edition/link-broken-1.svg"
				alt="fallback"
			/>
		</SVG>
	)
}
export default Icon

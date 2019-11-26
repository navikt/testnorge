import React from 'react'
import SVG from 'react-inlinesvg'
import cn from 'classnames'

import Trashcan from '~/assets/icons/nav-ikoner/line/SVG/01-edition/bin-1.svg'
import EditIcon from '~/assets/icons/nav-ikoner/line/SVG/01-edition/pencil-2.svg'
import Search from '~/assets/icons/nav-ikoner/line/SVG/01-edition/search.svg'
import Calendar from '~/assets/icons/nav-ikoner/line/SVG/22-time/calendar-3.svg'
import AddCircle from '~/assets/icons/nav-ikoner/line/SVG/19-interface/add-circle.svg'
import RemoveCircle from '~/assets/icons/nav-ikoner/line/SVG/19-interface/remove-circle.svg'
import Star from '~/assets/icons/nav-ikoner/line/SVG/05-votes-rewards/rank-army-star-1.svg'
import Eraser from '~/assets/icons/nav-ikoner/filled/SVG/36-text/eraser.svg'
import AccountCircle from '~/assets/icons/nav-ikoner/filled/SVG/11-users/account-circle.svg'
import NewFileTable from '~/assets/icons/nav-ikoner/line/SVG/17-files/file-table.svg'
import ArrowLeft from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-left-10.svg'
import ArrowRight from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-right-10.svg'
import Details from '~/assets/icons/custom/Details.svg'
import Synchronize from '~/assets/icons/nav-ikoner/filled/SVG/19-interface/synchronize-3.svg'
import ReportProblemCircle from '~/assets/icons/custom/ProblemCircle.svg'
import CheckCircle from '~/assets/icons/custom/CheckCircle.svg'
import ProblemTriangle from '~/assets/icons/custom/ProblemTriangle.svg'
import ChevronDown from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-down-8.svg'
import ChevronUp from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-up-8.svg'
import ChevronLeft from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-left-8.svg'
import ChevronRight from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-right-8.svg'
import Man from '~/assets/icons/nav-ikoner/line/SVG/11-users/man.svg'

import './Icon.less'

const icons = {
	trashcan: Trashcan,
	'add-circle': AddCircle,
	'remove-circle': RemoveCircle,
	edit: EditIcon,
	star: Star,
	'star-filled': Star,
	eraser: Eraser,
	user: AccountCircle,
	search: Search,
	calendar: Calendar,
	'file-new-table': NewFileTable,
	'arrow-left': ArrowLeft,
	'arrow-right': ArrowRight,
	details: Details,
	synchronize: Synchronize,
	'report-problem-circle': ReportProblemCircle,
	'feedback-check-circle': CheckCircle,
	'report-problem-triangle': ProblemTriangle,
	'chevron-down': ChevronDown,
	'chevron-up': ChevronUp,
	'chevron-left': ChevronLeft,
	'chevron-right': ChevronRight,
	man: Man
}

const px = v => `${v}px`

export default function Icon({ kind, size = 24, style, className }) {
	if (!icons[kind]) return null

	const cssClass = cn('svg-icon', `svg-icon-${kind}`, className)
	const styleObj = Object.assign({ width: px(size), height: px(size) }, style)

	return <SVG src={icons[kind]} className={cssClass} style={styleObj} />
}

import React from 'react'
import SVG from 'react-inlinesvg'
import cn from 'classnames'

import Trashcan from '~/assets/icons/nav-ikoner/line/SVG/01-edition/bin-1.svg'
import EditIcon from '~/assets/icons/nav-ikoner/line/SVG/01-edition/pencil-2.svg'
import Search from '~/assets/icons/nav-ikoner/line/SVG/01-edition/search.svg'
import Skull from '~/assets/icons/nav-ikoner/line/SVG/01-edition/skull-1.svg'
import ChatBubble from '~/assets/icons/nav-ikoner/line/SVG/06-comment-chat/bubble-chat-1.svg'
import Star from '~/assets/icons/nav-ikoner/line/SVG/05-votes-rewards/rank-army-star-1.svg'
import Envelope from '~/assets/icons/nav-ikoner/line/SVG/10-email/email-1.svg'
import EmailText from '~/assets/icons/nav-ikoner/line/SVG/10-email/email-text.svg'
import AccountCircle from '~/assets/icons/nav-ikoner/filled/SVG/11-users/account-circle.svg'
import Man from '~/assets/icons/nav-ikoner/line/SVG/11-users/man.svg'
import Woman from '~/assets/icons/nav-ikoner/line/SVG/11-users/lady-2.svg'
import Group from '~/assets/icons/nav-ikoner/line/SVG/11-users/account-group-4.svg'
import Family from '~/assets/icons/nav-ikoner/line/SVG/11-users/account-group-5.svg'
import Group1 from '~/assets/icons/nav-ikoner/line/SVG/11-users/account-group-1.svg'
import NewFileTable from '~/assets/icons/nav-ikoner/line/SVG/17-files/file-table.svg'
import FileChecklist from '~/assets/icons/nav-ikoner/line/SVG/17-files/file-checklist.svg'
// import NewFile from '~/assets/icons/nav-ikoner/line/SVG/17-files/file-new-1.svg'
import NewFile from '~/assets/icons/nav-ikoner/filled/SVG/17-files/file-add.svg'
import RectFilled from '~/assets/icons/nav-ikoner/filled/SVG/19-interface/filled-version-button-empty.svg'
import RectEmpty from '~/assets/icons/nav-ikoner/line/SVG/19-interface/line-version-expanded-button-empty.svg'
import Synchronize from '~/assets/icons/nav-ikoner/filled/SVG/19-interface/synchronize-3.svg'
import AddCircle from '~/assets/icons/nav-ikoner/line/SVG/19-interface/add-circle.svg'
import RemoveCircle from '~/assets/icons/nav-ikoner/line/SVG/19-interface/remove-circle.svg'
import CheckCircle2 from '~/assets/icons/nav-ikoner/line/SVG/20-status/check-circle-2.svg'
import Wrench from '~/assets/icons/nav-ikoner/line/SVG/21-settings/wrench.svg'
import Calendar from '~/assets/icons/nav-ikoner/line/SVG/22-time/calendar-3.svg'
import InformationCircle from '~/assets/icons/nav-ikoner/line/SVG/23-alerts-informations/information-circle.svg'
import BankNote from '~/assets/icons/nav-ikoner/line/SVG/24-business-finance/bank-notes-3.svg'
import Institusjon from '~/assets/icons/nav-ikoner/line/SVG/26-places/building-2.svg'
import House from '~/assets/icons/nav-ikoner/line/SVG/26-places/home-1.svg'
import Globe from '~/assets/icons/nav-ikoner/line/SVG/26-places/globe-1.svg'
import Globe2 from '~/assets/icons/nav-ikoner/line/SVG/26-places/globe-2.svg'
import Eraser from '~/assets/icons/nav-ikoner/filled/SVG/36-text/eraser.svg'
import ArrowLeft from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-left-10.svg'
import ArrowRight from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-right-10.svg'
import ChevronDown from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-down-8.svg'
import ChevronUp from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-up-8.svg'
import ChevronLeft from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-left-8.svg'
import ChevronRight from '~/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-right-8.svg'

import ProblemTriangle from '~/assets/icons/custom/ProblemTriangle.svg'
import Details from '~/assets/icons/custom/Details.svg'
import ReportProblemCircle from '~/assets/icons/custom/ProblemCircle.svg'
import CheckCircle from '~/assets/icons/custom/CheckCircle.svg'

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
	'rectangle-empty': RectEmpty,
	'rectangle-filled': RectFilled,
	man: Man,
	woman: Woman,
	group: Group,
	bestilling: FileChecklist,
	newFile: NewFile,

	personinformasjon: InformationCircle,
	nasjonalitet: Globe2,
	relasjoner: Family,
	identhistorikk: InformationCircle,
	identifikasjon: Group1,
	adresse: House,
	postadresse: EmailText,
	doedsbo: Skull,
	krr: Envelope,
	arena: CheckCircle2,
	institusjon: Institusjon,
	arbeid: Wrench,
	sigrun: BankNote,
	udi: Globe,
	kommentar: ChatBubble
}

const px = v => `${v}px`

export default function Icon({ kind, size = 24, style, className }) {
	if (!icons[kind]) return null

	const cssClass = cn('svg-icon', `svg-icon-${kind}`, className)
	const styleObj = Object.assign({ width: px(size), height: px(size) }, style)

	return <SVG src={icons[kind]} className={cssClass} style={styleObj} />
}

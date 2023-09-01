import SVG from 'react-inlinesvg'
import cn from 'classnames'

import {
	ArrowLeftIcon,
	ArrowRightIcon,
	ArrowsCirclepathIcon,
	BabyWrappedIcon,
	BagdeIcon,
	BandageIcon,
	BankNoteIcon,
	Buldings2Icon,
	Buldings3Icon,
	CalendarIcon,
	ChatElipsisIcon,
	CheckmarkCircleIcon,
	ChevronDownIcon,
	ChevronUpIcon,
	EarthIcon,
	EnvelopeClosedIcon,
	EraserIcon,
	FigureInwardFillIcon,
	FigureInwardIcon,
	FigureOutwardIcon,
	FileCheckmarkFillIcon,
	FileCheckmarkIcon,
	FileCodeIcon,
	FileLoadingIcon,
	FilePlusIcon,
	FilesIcon,
	FileTextIcon,
	FlowerPetalFallingIcon,
	HeartIcon,
	HourglassIcon,
	HouseIcon,
	InboxDownIcon,
	InformationSquareIcon,
	LeaveIcon,
	LinkBrokenIcon,
	LinkIcon,
	MagnifyingGlassIcon,
	MinusCircleIcon,
	PadlockLockedIcon,
	PencilIcon,
	PersonCircleIcon,
	PersonGroupFillIcon,
	PersonGroupIcon,
	PersonIcon,
	PersonTallShortIcon,
	PhoneIcon,
	PiggybankIcon,
	PlusCircleIcon,
	SackKronerIcon,
	SilhouetteFillIcon,
	SilhouetteIcon,
	StarFillIcon,
	StarIcon,
	TenancyFillIcon,
	TenancyIcon,
	ThumbDownIcon,
	ThumbUpIcon,
	TrashIcon,
	WrenchIcon,
	XMarkIcon,
} from '@navikt/aksel-icons'

import ProblemTriangle from '@/assets/icons/custom/ProblemTriangle.svg?raw'
import ReportProblemCircle from '@/assets/icons/custom/ProblemCircle.svg?raw'
import CheckCircle from '@/assets/icons/custom/CheckCircle.svg?raw'
import Dolly from '@/assets/icons/custom/Dolly.svg?raw'
import DollyPanic from '@/assets/icons/custom/DollyPanic.svg?raw'
import Cypress from '@/assets/img/cypress.png'

import './Icon.less'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'

export const icons = {
	'report-problem-circle': ReportProblemCircle,
	'feedback-check-circle': CheckCircle,
	'report-problem-triangle': ProblemTriangle,
	dolly: Dolly,
	dollyPanic: DollyPanic,
	cypress: Cypress,

	'designsystem-trashcan': TrashIcon,
	'designsystem-add-circle': PlusCircleIcon,
	'designsystem-remove-circle': MinusCircleIcon,
	'designsystem-edit': PencilIcon,
	'designsystem-star': StarIcon,
	'designsystem-star-dark': StarFillIcon,
	'designsystem-star-light': StarIcon,
	'designsystem-star-filled': StarFillIcon,
	'designsystem-eraser': EraserIcon,
	'designsystem-user': PersonCircleIcon,
	'designsystem-search': MagnifyingGlassIcon,
	'designsystem-calendar': CalendarIcon,
	'designsystem-file-new-table': FilePlusIcon,
	'designsystem-arrow-left': ArrowLeftIcon,
	'designsystem-arrow-right': ArrowRightIcon,
	'designsystem-details': InformationSquareIcon,
	'designsystem-synchronize': ArrowsCirclepathIcon,
	'designsystem-kryss': XMarkIcon,
	'chevron-down': ChevronDownIcon,
	'chevron-up': ChevronUpIcon,
	'designsystem-thumbs-up': ThumbUpIcon,
	'designsystem-thumbs-down': ThumbDownIcon,
	'designsystem-lock': PadlockLockedIcon,
	'designsystem-logout': LeaveIcon,

	'designsystem-man': FigureInwardFillIcon,
	'designsystem-man-light': FigureInwardIcon,
	'designsystem-man-silhouette': SilhouetteFillIcon,
	'designsystem-man-silhouette-light': SilhouetteIcon,
	'designsystem-woman': FigureOutwardIcon,
	'designsystem-person': PersonIcon,
	'designsystem-group': PersonGroupFillIcon,
	'designsystem-group-light': PersonGroupIcon,
	'designsystem-group-dark': PersonGroupFillIcon,
	'designsystem-locked-group': PadlockLockedIcon,
	'designsystem-sikkerhetstiltak': PadlockLockedIcon,
	'designsystem-bestilling': FileCheckmarkFillIcon,
	'designsystem-bestilling-light': FileCheckmarkIcon,
	'designsystem-new-file': FilePlusIcon,
	'designsystem-maler': FileLoadingIcon,

	'designsystem-personinformasjon': InformationSquareIcon,
	'designsystem-nasjonalitet': EarthIcon,
	'designsystem-relasjoner': PersonTallShortIcon,
	'designsystem-identhistorikk': BagdeIcon,
	'designsystem-identifikasjon': PersonGroupIcon,
	'designsystem-adresse': HouseIcon,
	'designsystem-postadresse': InboxDownIcon,
	'designsystem-midlertidig-adresse': HourglassIcon,
	'designsystem-doedsbo': HouseIcon,
	'designsystem-krr': EnvelopeClosedIcon,
	'designsystem-arena': CheckmarkCircleIcon,
	'designsystem-institusjon': Buldings3Icon,
	'designsystem-arbeid': WrenchIcon,
	'designsystem-sigrun': BankNoteIcon,
	'designsystem-bankkonto': BankNoteIcon,
	'designsystem-inntektstub': SackKronerIcon,
	'designsystem-inntektsmelding': BankNoteIcon,
	'designsystem-udi': EarthIcon,
	'designsystem-kommentar': ChatElipsisIcon,
	'designsystem-partner': HeartIcon,
	'designsystem-barn': BabyWrappedIcon,
	'designsystem-doedfoedt': FlowerPetalFallingIcon,
	'designsystem-pensjon': PiggybankIcon,
	'designsystem-brreg': Buldings2Icon,
	'designsystem-dokarkiv': FileTextIcon,
	'designsystem-copy': FilesIcon,
	'designsystem-sykdom': BandageIcon,
	'designsystem-vergemaal': PersonGroupIcon,
	'designsystem-vis-tps-data': MagnifyingGlassIcon,
	'designsystem-vis-org-data': MagnifyingGlassIcon,
	'designsystem-organisasjon': TenancyFillIcon,
	'designsystem-organisasjon-light': TenancyIcon,
	'designsystem-fullmakt': PersonGroupIcon,
	'designsystem-link': LinkIcon,
	'designsystem-link-broken': LinkBrokenIcon,
	'designsystem-telephone': PhoneIcon,
	'designsystem-foedsel': BabyWrappedIcon,
	'designsystem-foreldreansvar': PersonGroupIcon,
	'designsystem-grav': FlowerPetalFallingIcon,
	'designsystem-flytt': LeaveIcon,
	'designsystem-file-new': FileTextIcon,
	'designsystem-file-code': FileCodeIcon,
	'designsystem-cv': FileTextIcon,
}

const px = (v: number) => `${v}px`

const Icon = ({
	kind = null as unknown as string,
	title = undefined,
	size = 24,
	fontSize = '1rem',
	style = undefined,
	className = undefined as unknown as string,
	...props
}) => {
	const icon = icons?.[kind]
	if (!icon) {
		return null
	}
	const DesignSystemIcon = kind.includes('designsystem') && icon

	const cssClass = cn('svg-icon', `svg-icon-${kind}`, className)
	const styleObj = Object.assign({ width: px(size), height: px(size) }, style)

	return DesignSystemIcon ? (
		<DesignSystemIcon title={kind} fontSize={fontSize} className={className} />
	) : (
		<ErrorBoundary>
			<SVG src={icon} className={cssClass} style={styleObj} title={title} role={'img'} {...props}>
				<img
					src="../assets/icons/nav-ikoner/filled/SVG/01-edition/link-broken-1.svg"
					alt="fallback"
				/>
			</SVG>
		</ErrorBoundary>
	)
}
export default Icon

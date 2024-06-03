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
	CandleIcon,
	ChatElipsisIcon,
	CheckmarkCircleIcon,
	ChevronDownDoubleCircleIcon,
	ChevronDownIcon,
	ChevronUpDoubleCircleIcon,
	ChevronUpIcon,
	EarthIcon,
	EnvelopeClosedIcon,
	EraserIcon,
	FigureInwardIcon,
	FigureOutwardIcon,
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
	PlusCircleIcon,
	ReceiptIcon,
	SackKronerIcon,
	SackPensionIcon,
	SilhouetteIcon,
	StarFillIcon,
	StarIcon,
	TenancyIcon,
	ThumbDownFillIcon,
	ThumbUpFillIcon,
	TrashIcon,
	WrenchIcon,
	XMarkIcon,
} from '@navikt/aksel-icons'

import ProblemTriangle from '@/assets/icons/custom/ProblemTriangle.svg?raw'
import ReportProblemCircle from '@/assets/icons/custom/ProblemCircle.svg?raw'
import CheckCircle from '@/assets/icons/custom/CheckCircle.svg?raw'
import Dolly from '@/assets/icons/custom/Dolly.svg?raw'
import DollyPanic from '@/assets/icons/custom/DollyPanic.svg?raw'
import TenorLogo from '@/assets/icons/custom/TenorLogo.svg?raw'
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
	tenor: TenorLogo,

	trashcan: TrashIcon,
	'add-circle': PlusCircleIcon,
	'remove-circle': MinusCircleIcon,
	edit: PencilIcon,
	star: StarIcon,
	'star-dark': StarFillIcon,
	'star-light': StarIcon,
	'star-filled': StarFillIcon,
	eraser: EraserIcon,
	kvittering: ReceiptIcon,
	user: PersonCircleIcon,
	search: MagnifyingGlassIcon,
	calendar: CalendarIcon,
	'file-new-table': FilePlusIcon,
	'arrow-left': ArrowLeftIcon,
	'arrow-right': ArrowRightIcon,
	details: InformationSquareIcon,
	synchronize: ArrowsCirclepathIcon,
	kryss: XMarkIcon,
	'chevron-down': ChevronDownIcon,
	'chevron-up': ChevronUpIcon,
	'chevron-down-double-circle': ChevronDownDoubleCircleIcon,
	'chevron-up-double-circle': ChevronUpDoubleCircleIcon,
	'thumbs-up': ThumbUpFillIcon,
	'thumbs-down': ThumbDownFillIcon,
	lock: PadlockLockedIcon,
	logout: LeaveIcon,

	man: FigureInwardIcon,
	'man-light': FigureInwardIcon,
	'man-silhouette': SilhouetteIcon,
	woman: FigureOutwardIcon,
	person: PersonIcon,
	ukjent: SilhouetteIcon,
	group: PersonGroupIcon,
	'group-light': PersonGroupIcon,
	'group-dark': PersonGroupFillIcon,
	'locked-group': PadlockLockedIcon,
	sikkerhetstiltak: PadlockLockedIcon,
	bestilling: FileCheckmarkIcon,
	'bestilling-light': FileCheckmarkIcon,
	'new-file': FilePlusIcon,
	maler: FileLoadingIcon,

	personinformasjon: InformationSquareIcon,
	nasjonalitet: EarthIcon,
	relasjoner: PersonTallShortIcon,
	identhistorikk: BagdeIcon,
	identifikasjon: PersonGroupIcon,
	adresse: HouseIcon,
	postadresse: InboxDownIcon,
	'midlertidig-adresse': HourglassIcon,
	doedsbo: CandleIcon,
	krr: EnvelopeClosedIcon,
	arena: CheckmarkCircleIcon,
	institusjon: Buldings3Icon,
	arbeid: WrenchIcon,
	sigrun: BankNoteIcon,
	bankkonto: BankNoteIcon,
	pengesekk: SackKronerIcon,
	inntektsmelding: BankNoteIcon,
	udi: EarthIcon,
	kommentar: ChatElipsisIcon,
	partner: HeartIcon,
	barn: BabyWrappedIcon,
	doedfoedt: FlowerPetalFallingIcon,
	pensjon: SackPensionIcon,
	brreg: Buldings2Icon,
	dokarkiv: FileTextIcon,
	copy: FilesIcon,
	sykdom: BandageIcon,
	vergemaal: PersonGroupIcon,
	'vis-tps-data': MagnifyingGlassIcon,
	'vis-org-data': MagnifyingGlassIcon,
	organisasjon: TenancyIcon,
	fullmakt: PersonGroupIcon,
	link: LinkIcon,
	'link-broken': LinkBrokenIcon,
	telephone: PhoneIcon,
	foedsel: BabyWrappedIcon,
	foreldreansvar: PersonGroupIcon,
	grav: FlowerPetalFallingIcon,
	flytt: LeaveIcon,
	'file-new': FileTextIcon,
	'file-code': FileCodeIcon,
	cv: FileTextIcon,
}

const px = (v: number) => `${v}px`

const Icon = ({
	kind = null as unknown as string,
	title = undefined,
	size = 24,
	fontSize = '1rem',
	style = undefined as unknown as React.CSSProperties,
	className = undefined as unknown as string,
	onClick = null as unknown as any,
	...props
}) => {
	const icon = icons?.[kind]
	if (!icon) {
		return null
	}

	const halvannenRemIkoner = ['chevron-up', 'chevron-down', 'kommentar']

	const egneIkon = [
		'report-problem-circle',
		'feedback-check-circle',
		'report-problem-triangle',
		'dolly',
		'dollyPanic',
		'cypress',
		'tenor',
	]

	if (halvannenRemIkoner.includes(kind)) {
		fontSize = '1.5rem'
	}

	const DesignSystemIcon = !egneIkon.includes(kind) && icon

	const cssClass = cn('svg-icon', `svg-icon-${kind}`, className)
	const styleObj = Object.assign({ width: px(size), height: px(size) }, style)

	return DesignSystemIcon ? (
		<DesignSystemIcon
			title={kind}
			fontSize={fontSize}
			className={className}
			data-testid={props['data-testid']}
			onClick={onClick}
			style={style}
		/>
	) : (
		<ErrorBoundary>
			<SVG
				src={icon}
				className={cssClass}
				style={styleObj}
				title={title}
				role={'img'}
				onClick={onClick}
				{...props}
			>
				<img src={LinkBrokenIcon} alt="fallback" />
			</SVG>
		</ErrorBoundary>
	)
}
export default Icon

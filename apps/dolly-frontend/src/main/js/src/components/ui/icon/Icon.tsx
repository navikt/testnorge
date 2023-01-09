import SVG from 'react-inlinesvg'
import cn from 'classnames'

import Trashcan from '@/assets/icons/nav-ikoner/line/SVG/01-edition/bin-1.svg?raw'
import EditIcon from '@/assets/icons/nav-ikoner/line/SVG/01-edition/pencil-2.svg?raw'
import Search from '@/assets/icons/nav-ikoner/line/SVG/01-edition/search.svg?raw'
import Skull from '@/assets/icons/nav-ikoner/line/SVG/01-edition/skull-1.svg?raw'
import Lock from '@/assets/icons/nav-ikoner/line/SVG/01-edition/line-version-lock-close-2.svg?raw'
import Lock_Black from '@/assets/icons/nav-ikoner/line/SVG/01-edition/lock-black.svg?raw'
import ChatBubble from '@/assets/icons/nav-ikoner/line/SVG/06-comment-chat/bubble-chat-1.svg?raw'
import Telephone from '@/assets/icons/nav-ikoner/line/SVG/06-comment-chat/Telephone.svg?raw'
import ThumbsUp from '@/assets/icons/nav-ikoner/line/SVG/05-votes-rewards/filled-version-thumbs-up-2.svg?raw'
import ThumbsDown from '@/assets/icons/nav-ikoner/line/SVG/05-votes-rewards/filled-version-thumbs-down-2.svg?raw'
import Star from '@/assets/icons/nav-ikoner/line/SVG/05-votes-rewards/rank-army-star-1.svg?raw'
import StarDark from '@/assets/icons/nav-ikoner/line/SVG/05-votes-rewards/rank-army-star-1-black.svg?raw'
import StarLight from '@/assets/icons/nav-ikoner/line/SVG/05-votes-rewards/rank-army-star-1-white.svg?raw'
import Envelope from '@/assets/icons/nav-ikoner/line/SVG/10-email/email-1.svg?raw'
import EmailText from '@/assets/icons/nav-ikoner/line/SVG/10-email/email-text.svg?raw'
import AccountCircle from '@/assets/icons/nav-ikoner/filled/SVG/11-users/account-circle.svg?raw'
import Group from '@/assets/icons/nav-ikoner/line/SVG/11-users/account-group-4.svg?raw'
import GroupLight from '@/assets/icons/nav-ikoner/line/SVG/11-users/account-group-4Light.svg?raw'
import GroupDark from '@/assets/icons/nav-ikoner/line/SVG/11-users/account-group-4Dark.svg?raw'
import Family from '@/assets/icons/nav-ikoner/line/SVG/11-users/account-group-5.svg?raw'
import Group1 from '@/assets/icons/nav-ikoner/line/SVG/11-users/account-group-1.svg?raw'
import Group2 from '@/assets/icons/nav-ikoner/line/SVG/11-users/account-group-2.svg?raw'
import IdCardDouble from '@/assets/icons/nav-ikoner/line/SVG/11-users/id-card-double.svg?raw'
import AccountFind from '@/assets/icons/nav-ikoner/line/SVG/11-users/account-find-2.svg?raw'
import NewFileTable from '@/assets/icons/nav-ikoner/line/SVG/17-files/file-table.svg?raw'
import FileChecklist from '@/assets/icons/nav-ikoner/line/SVG/17-files/file-checklist.svg?raw'
import FileChecklistLight from '@/assets/icons/nav-ikoner/line/SVG/17-files/file-checklistLight.svg?raw'
import FileRefresh from '@/assets/icons/nav-ikoner/line/SVG/17-files/file-refresh.svg?raw'
import Copy from '@/assets/icons/nav-ikoner/line/SVG/17-files/copy-1.svg?raw'
import NewFile from '@/assets/icons/nav-ikoner/filled/SVG/17-files/file-add.svg?raw'
import NewFile2 from '@/assets/icons/nav-ikoner/line/SVG/17-files/file-new-2.svg'
import FileCode from '@/assets/icons/nav-ikoner/line/SVG/17-files/file-code.svg'
import Files from '@/assets/icons/nav-ikoner/line/SVG/17-files/files-3.svg?raw'
import Synchronize from '@/assets/icons/nav-ikoner/filled/SVG/19-interface/synchronize-3.svg?raw'
import Kryss from '@/assets/icons/nav-ikoner/filled/SVG/19-interface/kryss.svg?raw'
import AddCircle from '@/assets/icons/nav-ikoner/line/SVG/19-interface/add-circle.svg?raw'
import RemoveCircle from '@/assets/icons/nav-ikoner/line/SVG/19-interface/remove-circle.svg?raw'
import Logout from '@/assets/icons/nav-ikoner/line/SVG/19-interface/logout.svg?raw'
import CheckCircle2 from '@/assets/icons/nav-ikoner/line/SVG/20-status/check-circle-2.svg?raw'
import Wrench from '@/assets/icons/nav-ikoner/line/SVG/21-settings/wrench.svg?raw'
import Calendar from '@/assets/icons/nav-ikoner/line/SVG/22-time/calendar-3.svg?raw'
import Hourglass from '@/assets/icons/nav-ikoner/line/SVG/22-time/hourglass-1.svg?raw'
import InformationCircle from '@/assets/icons/nav-ikoner/line/SVG/23-alerts-informations/information-circle.svg?raw'
import BankNote from '@/assets/icons/nav-ikoner/line/SVG/24-business-finance/bank-notes-3.svg?raw'
import MoneyBag from '@/assets/icons/nav-ikoner/line/SVG/24-business-finance/money-bag-dollar.svg?raw'
import CoinBankNote from '@/assets/icons/nav-ikoner/line/SVG/24-business-finance/coin-bank-note.svg?raw'
import Institusjon from '@/assets/icons/nav-ikoner/line/SVG/26-places/building-2.svg?raw'
import House from '@/assets/icons/nav-ikoner/line/SVG/26-places/home-1.svg?raw'
import Globe from '@/assets/icons/nav-ikoner/line/SVG/26-places/globe-1.svg?raw'
import Globe2 from '@/assets/icons/nav-ikoner/line/SVG/26-places/globe-2.svg?raw'
import Hierarchy3 from '@/assets/icons/nav-ikoner/line/SVG/33-hierarchy/hierarchy-3.svg?raw'
import Hierarchy3Light from '@/assets/icons/nav-ikoner/line/SVG/33-hierarchy/hierarchy-3Light.svg?raw'
import Infants from '@/assets/icons/nav-ikoner/line/SVG/people/Infants.svg?raw'
import Eraser from '@/assets/icons/nav-ikoner/filled/SVG/36-text/eraser.svg?raw'
import ArrowLeft from '@/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-left-10.svg?raw'
import ArrowRight from '@/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-right-10.svg?raw'
import ChevronDown from '@/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-down-8.svg?raw'
import ChevronUp from '@/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-up-8.svg?raw'
import ChevronLeft from '@/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-left-8.svg?raw'
import ChevronRight from '@/assets/icons/nav-ikoner/filled/SVG/46-arrows/arrow-right-8.svg?raw'
import PiggyBank from '@/assets/icons/nav-ikoner/line/SVG/24-business-finance/piggy-bank.svg?raw'
import Plaster from '@/assets/icons/nav-ikoner/line/SVG/40-health/first-aid-plaster.svg?raw'
import Link from '@/assets/icons/nav-ikoner/filled/SVG/01-edition/link-2.svg?raw'
import LinkBroken from '@/assets/icons/nav-ikoner/filled/SVG/01-edition/link-broken-1.svg?raw'

import ProblemTriangle from '@/assets/icons/custom/ProblemTriangle.svg?raw'
import Details from '@/assets/icons/custom/Details.svg?raw'
import ReportProblemCircle from '@/assets/icons/custom/ProblemCircle.svg?raw'
import CheckCircle from '@/assets/icons/custom/CheckCircle.svg?raw'
import Man from '@/assets/icons/custom/Man.svg?raw'
import ManLight from '@/assets/icons/custom/ManLight.svg?raw'
import Man2 from '@/assets/icons/custom/Man2.svg?raw'
import Man2Light from '@/assets/icons/custom/Man2Light.svg?raw'
import Woman from '@/assets/icons/custom/Woman.svg?raw'
import Person from '@/assets/icons/custom/Person.svg?raw'
import Love from '@/assets/icons/custom/Love.svg?raw'
import Baby from '@/assets/icons/custom/Baby.svg?raw'
import ChildHalo from '@/assets/icons/custom/ChildHalo2.svg?raw'
import Dolly from '@/assets/icons/custom/Dolly.svg?raw'
import DollyPanic from '@/assets/icons/custom/DollyPanic.svg?raw'
import Cypress from '@/assets/img/cypress.png'
import BrregLogo from '@/assets/icons/custom/Brreg_logo.svg?raw'
import LockedGroup from '@/assets/icons/custom/LockedGroup.svg?raw'
import RIP from '@/assets/icons/custom/RIP.svg?raw'
import Flytt from '@/assets/icons/custom/Flytt.svg?raw'

import './Icon.less'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

export const icons = {
	trashcan: Trashcan,
	'add-circle': AddCircle,
	'remove-circle': RemoveCircle,
	edit: EditIcon,
	star: Star,
	starDark: StarDark,
	starLight: StarLight,
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
	kryss: Kryss,
	'report-problem-circle': ReportProblemCircle,
	'feedback-check-circle': CheckCircle,
	'report-problem-triangle': ProblemTriangle,
	'chevron-down': ChevronDown,
	'chevron-up': ChevronUp,
	'chevron-left': ChevronLeft,
	'chevron-right': ChevronRight,
	expand: ChevronDown,
	collapse: ChevronUp,
	ThumbsUp: ThumbsUp,
	ThumbsDown: ThumbsDown,
	lock: Lock_Black,
	logout: Logout,

	man: Man,
	manLight: ManLight,
	man2: Man2,
	man2Light: Man2Light,
	woman: Woman,
	person: Person,
	group: Group,
	groupLight: GroupLight,
	groupDark: GroupDark,
	lockedGroup: LockedGroup,
	sikkerhetstiltak: Lock,
	bestilling: FileChecklist,
	bestillingLight: FileChecklistLight,
	newFile: NewFile,
	maler: FileRefresh,

	personinformasjon: InformationCircle,
	nasjonalitet: Globe2,
	relasjoner: Family,
	identhistorikk: IdCardDouble,
	identifikasjon: Group1,
	adresse: House,
	postadresse: EmailText,
	midlertidigAdresse: Hourglass,
	doedsbo: Skull,
	krr: Envelope,
	arena: CheckCircle2,
	institusjon: Institusjon,
	arbeid: Wrench,
	sigrun: BankNote,
	bankkonto: BankNote,
	inntektstub: MoneyBag,
	inntektsmelding: CoinBankNote,
	udi: Globe,
	kommentar: ChatBubble,
	partner: Love,
	barn: Baby,
	doedfoedt: ChildHalo,
	dolly: Dolly,
	dollyPanic: DollyPanic,
	cypress: Cypress,
	pensjon: PiggyBank,
	brreg: BrregLogo,
	dokarkiv: Files,
	copy: Copy,
	sykdom: Plaster,
	vergemaal: Group2,
	visTpsData: AccountFind,
	visOrgData: Search,
	organisasjon: Hierarchy3,
	organisasjonLight: Hierarchy3Light,
	fullmakt: Group2,
	link: Link,
	linkBroken: LinkBroken,
	telephone: Telephone,
	foedsel: Infants,
	foreldreansvar: Group2,
	grav: RIP,
	flytt: Flytt,
	fileNew2: NewFile2,
	fileCode: FileCode,
}

const px = (v: number) => `${v}px`

const Icon = ({
	kind = null,
	title = undefined,
	size = 24,
	style = undefined,
	className = undefined,
	...props
}) => {
	if (!icons?.[kind]) {
		return null
	}

	const cssClass = cn('svg-icon', `svg-icon-${kind}`, className)
	const styleObj = Object.assign({ width: px(size), height: px(size) }, style)

	return (
		<ErrorBoundary>
			<SVG
				src={icons[kind]}
				className={cssClass}
				style={styleObj}
				title={title}
				role={'img'}
				{...props}
			>
				<img
					src="../assets/icons/nav-ikoner/filled/SVG/01-edition/link-broken-1.svg"
					alt="fallback"
				/>
			</SVG>
		</ErrorBoundary>
	)
}
export default Icon

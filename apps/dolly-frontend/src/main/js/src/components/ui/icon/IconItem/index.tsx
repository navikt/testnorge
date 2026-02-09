import './IconItem.less'
import { IconItem } from '@/components/ui/icon/IconItem/IconItem'
import {
	FigureInwardIcon,
	FigureOutwardIcon,
	FileCheckmarkIcon,
	PadlockLockedIcon,
	PersonGroupIcon,
	SilhouetteIcon,
	TenancyIcon,
} from '@navikt/aksel-icons'

const WomanIconItem = () => (
	<IconItem className={'icon-item--woman'} icon={<FigureOutwardIcon fontSize="2rem" />} />
)
const ManIconItem = () => (
	<IconItem className={'icon-item--man'} icon={<FigureInwardIcon fontSize="2rem" />} />
)
const UnknownIconItem = () => (
	<IconItem className={'icon-item--unknown'} icon={<SilhouetteIcon fontSize="2rem" />} />
)
const BestillingIconItem = () => (
	<IconItem className={'icon-item--bestilling'} icon={<FileCheckmarkIcon fontSize="2rem" />} />
)
const GruppeIconItem = () => (
	<IconItem className={'icon-item--group'} icon={<PersonGroupIcon fontSize="2rem" />} />
)
const LaastGruppeIconItem = () => (
	<IconItem className={'icon-item--unknown'} icon={<PadlockLockedIcon fontSize="2rem" />} />
)
const OrganisasjonItem = () => (
	<IconItem className={'icon-item--org'} icon={<TenancyIcon fontSize="2rem" />} />
)

export {
	WomanIconItem,
	ManIconItem,
	UnknownIconItem,
	BestillingIconItem,
	GruppeIconItem,
	LaastGruppeIconItem,
	OrganisasjonItem,
}

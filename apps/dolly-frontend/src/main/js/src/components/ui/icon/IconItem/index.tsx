import './IconItem.less'
import { IconItem } from '@/components/ui/icon/IconItem/IconItem'

const WomanIconItem = () => (
	<IconItem className={'icon-item--woman'} icon={'woman'} fontSize={'2rem'} />
)
const ManIconItem = () => <IconItem className={'icon-item--man'} icon={'man'} fontSize={'2rem'} />
const UnknownIconItem = () => (
	<IconItem className={'icon-item--unknown'} icon={'ukjent'} fontSize={'2rem'} />
)
const BestillingIconItem = () => (
	<IconItem className={'icon-item--bestilling'} icon={'bestilling'} fontSize={'1.5rem'} />
)
const GruppeIconItem = () => (
	<IconItem className={'icon-item--group'} icon={'group'} fontSize={'2rem'} />
)
const LaastGruppeIconItem = () => (
	<IconItem className={'icon-item--group'} icon={'locked-group'} fontSize={'2rem'} />
)
const OrganisasjonItem = () => (
	<IconItem className={'icon-item--org'} icon={'organisasjon'} fontSize={'2rem'} />
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

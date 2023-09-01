import './IconItem.less'
import IconItem from './IconItem'

const WomanIconItem = () => (
	<IconItem className={'icon-item--woman'} icon={'designsystem-woman'} fontSize={'1.5rem'} />
)
const ManIconItem = () => (
	<IconItem className={'icon-item--man'} icon={'designsystem-man'} fontSize={'1.5rem'} />
)
const UnknownIconItem = () => (
	<IconItem className={'icon-item--unknown'} icon={'designsystem-person'} fontSize={'1.5rem'} />
)
const BestillingIconItem = () => (
	<IconItem
		className={'icon-item--bestilling'}
		icon={'designsystem-bestilling'}
		fontSize={'1.5rem'}
	/>
)
const GruppeIconItem = () => (
	<IconItem className={'icon-item--group'} icon={'designsystem-group'} fontSize={'1.5rem'} />
)
const LaastGruppeIconItem = () => (
	<IconItem className={'icon-item--group'} icon={'designsystem-locked-group'} fontSize={'1.5rem'} />
)
const OrganisasjonItem = () => (
	<IconItem className={'icon-item--org'} icon={'organisasjon'} fontSize={'1.5rem'} />
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

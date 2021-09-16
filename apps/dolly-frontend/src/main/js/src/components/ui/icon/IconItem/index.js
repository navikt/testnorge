import React from 'react'

import './IconItem.less'
import IconItem from './IconItem'

const WomanIconItem = () => <IconItem className={'icon-item--woman'} icon={'woman'} />
const ManIconItem = () => <IconItem className={'icon-item--man'} icon={'man'} />
const BestillingIconItem = () => (
	<IconItem className={'icon-item--bestilling'} icon={'bestilling'} />
)
const GruppeIconItem = () => <IconItem className={'icon-item--group'} icon={'group'} />
const LaastGruppeIconItem = () => <IconItem className={'icon-item--group'} icon={'lockedGroup'} />
const MalIconItem = () => <IconItem className={'icon-item'} icon={'maler'} />
const OrganisasjonItem = () => <IconItem className={'icon-item--org'} icon={'organisasjon'} />

export {
	WomanIconItem,
	ManIconItem,
	BestillingIconItem,
	GruppeIconItem,
	LaastGruppeIconItem,
	MalIconItem,
	OrganisasjonItem,
}

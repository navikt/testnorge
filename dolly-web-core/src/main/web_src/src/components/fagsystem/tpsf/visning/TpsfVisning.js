import React from 'react'
import { useSelector } from 'react-redux'
import '~/components/fagsystem/fagsystemVisning/fagsystemVisning.less'
import Personinfo from './Personinfo'
import Nasjonalitet from './Nasjonalitet'
import Boadresse from './Boadresse'
import Postadresse from './Postadresse'
import Identhistorikk from './Identhistorikk'
import Relasjoner from './Relasjoner'

export default function TpsfVisning(props) {
	const data = useSelector(state => state)
	const tpsfData = data.testbruker.items.tpsf.find(({ ident }) => ident === props.personId)

	const bestillingData = data.bestillingStatuser.data.find(
		({ id }) => id === parseInt(props.bestillingId)
	)
	const tpsfKriterier = JSON.parse(bestillingData.tpsfKriterier)

	if (!tpsfData) return null

	return (
		<div>
			<Personinfo tpsfData={tpsfData} tpsfKriterier={tpsfKriterier} />
			<Nasjonalitet tpsfData={tpsfData} tpsfKriterier={tpsfKriterier} />
			<Boadresse boadresse={tpsfData.boadresse} />
			<Postadresse postadresse={tpsfData.postadresse && tpsfData.postadresse[0]} />
			<Identhistorikk identhistorikk={tpsfData.identHistorikk} />
			<Relasjoner relasjoner={tpsfData.relasjoner} tpsfKriterier={tpsfKriterier} />
		</div>
	)
}

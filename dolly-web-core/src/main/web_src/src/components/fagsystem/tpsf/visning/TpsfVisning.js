import React from 'react'
import { useSelector } from 'react-redux'
import '~/components/fagsystem/fagsystemVisning/fagsystemVisning.less'
// import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'
// import Formatters from '~/utils/DataFormatter'
// import { relasjonTranslator } from '~/service/dataMapper/Utils'
import Persondetaljer from './Persondetaljer'
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
	console.log('tpsfData :', tpsfData)

	return (
		<div>
			{/* PERSONDETALJER */}
			<Persondetaljer tpsfData={tpsfData} tpsfKriterier={tpsfKriterier} />

			{/* NASJONALITET */}
			<Nasjonalitet tpsfData={tpsfData} tpsfKriterier={tpsfKriterier} />

			{/* BOADRESSE */}
			<Boadresse boadresse={tpsfData.boadresse} />

			{/* POSTADRESSE */}
			<Postadresse postadresse={tpsfData.postadresse && tpsfData.postadresse[0]} />

			{/* IDENTHISTORIKK */}
			<Identhistorikk identhistorikk={tpsfData.identHistorikk} />

			{/* RELASJONER */}
			<Relasjoner relasjoner={tpsfData.relasjoner} tpsfKriterier={tpsfKriterier} />
		</div>
	)
}

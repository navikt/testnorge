import React, { useEffect } from 'react'
import { useSelector, useReducer } from 'react-redux'
import TpsfVisning from '~/components/fagsystem/tpsf/visning/TpsfVisning'
import KrrVisning from '~/components/fagsystem/krrstub/visning/KrrVisning'
import Button from '~/components/ui/button/Button'
import '~/pages/gruppe/PersonDetaljer/PersonDetaljer.less' // Flytte denne?

export default function PersonDetaljer(props) {
	useEffect(() => {
		props.getDataFraFagsystemer()
	}, [])

	const tidligereBestilling = props.personData.find(
		data => data.header === 'Tidligere bestilling-ID'
	)

	const krrstub = props.bestilling.status.find(status => status.id === 'KRRSTUB')
	const krrstubStatus = krrstub && krrstub.statuser[0].melding

	// Sjekk resten av statusene!

	return (
		<div className="person-details">
			<TpsfVisning personId={props.personId} bestillingId={props.bestillingId} />
			{/* <PdlVisning /> */}
			{/* <SigrunVisning /> */}
			{krrstubStatus === 'OK' && (
				<KrrVisning
					personId={props.personId}
					bestillingId={props.bestillingId}
					isFetchingKrr={props.isFetchingKrr}
				/>
			)}
			{/* <AaregVisning /> */}
			{/* <InstVisning /> */}
			{/* <ArenaVisning /> */}
			{/* <UdiVisning /> */}
			{tidligereBestilling && (
				<div className="tidligere-bestilling-panel">
					<h4>Tidligere bestilling-ID</h4>
					<div>{tidligereBestilling.data[0].value}</div>
				</div>
			)}
			<div className="flexbox--align-center--justify-end">
				<Button className="flexbox--align-center" kind="details">
					BESTILLINGSDETALJER
				</Button>
				<Button className="flexbox--align-center" kind="edit">
					REDIGER
				</Button>
				<Button className="flexbox--align-center" kind="trashcan">
					SLETT
				</Button>
			</div>
		</div>
	)
}

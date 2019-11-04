// import React from 'react'
import React, { PureComponent, useEffect } from 'react'
import { useSelector, useReducer } from 'react-redux'
import TpsfVisning from '~/components/fagsystem/tpsf/visning/TpsfVisning'
import KrrVisning from '~/components/fagsystem/krr/visning/KrrVisning'
import '~/pages/gruppe/PersonDetaljer/PersonDetaljer.less' // flytte denne

export default function PersonDetaljer(props) {
	// const data = useSelector(state => state)
	// console.log('data :', data)
	// console.log('props 1:', props)
	// const [state, dispatch] = useReducer(reducer, initialArg, init)
	// console.log('state :', state)
	// console.log('dispatch :', dispatch)

	// Sjekk først om statuser på ulike miljøer er ok?
	// typ this.props.testIdent.sigrunstubStatus === 'OK &&

	// Sjekke status her?
	// data.gruppe.data[0].testidenter.find(testIdent => testident.ident === props.personId)

	// ta inn flere greier fra persondetaljerconnector??

	// const status = data.gruppe.data[0].identer.find(testIdent => testIdent.ident === props.personId)
	// console.log('status :', status)

	// useEffect(() => {
	// 	if (props.testIdent.sigrunstubStatus === 'OK' && data.testbruker.items.sigrunstub === null) {
	// 		props.getSigrunTestbruker()
	// 	}
	// })
	useEffect(() => {
		props.getDataFraFagsystemer()
	}, [])

	const krrstub = props.bestilling.status.find(status => status.id === 'KRRSTUB')
	const krrstubStatus = krrstub && krrstub.statuser[0].melding
	// Må man sjekke flere statuser her???
	console.log('krrstubStatus :', krrstubStatus)
	// const krrData = props.personData.find(data => data.header === 'Kontaktinformasjon og reservasjon')

	console.log('props:', props)

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
					// krrData={krrData}
					// getKrrTestbruker={props.getKrrTestbruker}
				/>
			)}
			{/* <AaregVisning /> */}
			{/* {props.testIdent.krrstubStatus === 'OK' && ( */}
			{/* <InstVisning /> */}
			{/* <ArenaVisning /> */}
			{/* <UdiVisning /> */}
		</div>
	)
}

import React, { useEffect } from 'react'
import { useSelector } from 'react-redux'
import '~/pages/gruppe/PersonDetaljer/PersonDetaljer.less' // flytte denne
import '~/components/personInfoBlock/personInfoBlock.less' //flytte denne
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export default function KrrVisning(props) {
	console.log('KRR props :', props)
	const data = useSelector(state => state)
	console.log('KRR data :', data)

	// useEffect(() => {
	// 	props.getKrrTestbruker()
	// }, [])

	const krrData = data.testbruker.items.krrstub && data.testbruker.items.krrstub[props.personId]
	// const krrData = props.krrData
	console.log('krrData :', krrData)

	return (
		<div className="person-details_content">
			<h3 className="flexbox--align-center">Kontaktinformasjon og reservasjon</h3>
			{props.isFetchingKrr && <Loading label="Henter data fra Krr" panel />}
			{krrData && (
				<div className="person-info-block_content">
					<div className="static-value">
						<h4>Mobilnummer</h4>
						<span>{krrData.mobil}</span>
					</div>
					<div className="static-value">
						<h4>E-post</h4>
						<span>{krrData.epost}</span>
					</div>
					<div className="static-value">
						<h4>Reservert mot digitalkommunikasjon</h4>
						<span>{Formatters.oversettBoolean(krrData.reservert)}</span>
					</div>
					<div className="static-value">
						<h4>Gyldig fra</h4>
						<span>{Formatters.formatDate(krrData.gyldigFra)}</span>
					</div>
					<div className="static-value">
						<h4>Registrert i DKIF</h4>
						<span>{Formatters.oversettBoolean(krrData.registrert)}</span>
					</div>
				</div>
			)}
		</div>
	)
}

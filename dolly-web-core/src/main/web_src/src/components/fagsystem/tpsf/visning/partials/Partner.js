import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Partner = ({ data, bestilling }) => {
	if (!data) return false
	console.log('data :', data)
	return (
		<div className="person-visning_content">
			<TitleValue title={data.identtype} value={data.ident} />
			<TitleValue title="Fornavn" value={data.fornavn} />
			<TitleValue title="Mellomnavn" value={data.mellomnavn} />
			<TitleValue title="Etternavn" value={data.etternavn} />
			<TitleValue title="Kjønn" value={Formatters.kjonn(data.kjonn, data.alder)} />
			<TitleValue title="Alder" value={Formatters.formatAlder(data.alder, data.doedsdato)} />
			{/* Diskresjonskode */}
			{/* ufb */}
			{/* kommunenr */}
			{/* adresse */}
			{/* FOrhold-array m/sivilstand og sivilstand fra dato */}
		</div>
	)
	// return (
	// 	<div>
	// 		<SubOverskrift label="Partner" />
	// 		{data.map((partnerInfo, idx) => (
	// 			<div className="person-visning_content" key={idx}>
	// 				<TitleValue title="PartnerNr" value={`#${idx + 1}`} size="x-small" />
	// 				<TitleValue
	// 					title="Kjønn"
	// 					value={Formatters.showLabel('Identtype', partnerInfo.identtype)}
	// 				/>
	// 				<TitleValue title="Forhold" value={Formatters.showLabel('Kjønn', partnerInfo.kjonn)} />
	// 				<TitleValue
	// 					title="Forhold"
	// 					value={Formatters.showLabel('Forhold', partnerInfo.sivilstand)}
	// 				/>
	// 				<TitleValue
	// 					title="Adresse"
	// 					value={Formatters.showLabel('Adresse', partnerInfo.harFellesAdresse)}
	// 				/>
	// 			</div>
	// 		))}
	// 	</div>
	// )
}

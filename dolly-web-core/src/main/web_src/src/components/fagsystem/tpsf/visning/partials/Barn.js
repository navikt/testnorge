import React, { useEffect, useState } from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { TpsfApi } from '~/service/Api'
import { Adressevisning } from './Boadresse'
import { Historikk } from '~/components/ui/historikk/Historikk'

export const Barn = ({ data, type }) => {
	if (!data) return false
	const [barnInfo, setBarnInfo] = useState(null)
	const [isLoading, setIsLoading] = useState(false)

	useEffect(() => {
		const fetchData = async () => {
			setIsLoading(true)
			const respons = await TpsfApi.getPersoner([data.ident])
			setBarnInfo(respons.data)
			setIsLoading(false)
		}
		fetchData()
	}, [])

	return (
		<div className="person-visning_content">
			<TitleValue title={data.identtype} value={data.ident} />
			<TitleValue title="Fornavn" value={data.fornavn} />
			<TitleValue title="Mellomnavn" value={data.mellomnavn} />
			<TitleValue title="Etternavn" value={data.etternavn} />
			<TitleValue title="Kjønn" value={Formatters.kjonn(data.kjonn, data.alder)} />
			<TitleValue title="Alder" value={Formatters.formatAlder(data.alder, data.doedsdato)} />
			<TitleValue title="Diskresjonskode" value={Formatters.showLabel(data.spesreg)} />
			<TitleValue title="Uten fast bopel" value={data.utenFastBopel && 'Ja'} />
			{barnInfo && !isLoading && (
				<TitleValue title="Foreldre" value={finnForeldre(barnInfo[0].relasjoner).join(', ')} />
			)}
			<TitleValue title="Er adoptert" value={Formatters.oversettBoolean(type === 'BARN')} />
			{!data.utenFastBopel && (
				<Historikk component={Adressevisning} propName="boadresse" data={data.boadresse} />
			)}
		</div>
	)
}

const finnForeldre = relasjoner => {
	return relasjoner
		.filter(relasjon => {
			return relasjon.relasjonTypeNavn === 'MOR' || relasjon.relasjonTypeNavn === 'FAR'
		})
		.map(relasjon => relasjon.personRelasjonMed.ident)
}

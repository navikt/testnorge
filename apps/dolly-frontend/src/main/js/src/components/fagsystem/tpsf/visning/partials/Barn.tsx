import React, { useEffect, useState } from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatAlderBarn, formatDate, formatKjonn, oversettBoolean } from '@/utils/DataFormatter'
import { TpsMessagingApi } from '@/service/Api'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'
import { Adressevalg } from '@/components/fagsystem/tpsf/visning/partials/Adressevalg'

export const Barn = ({ data, type }) => {
	const [barnInfo, setBarnInfo] = useState(null)
	const [isLoading, setIsLoading] = useState(false)

	useEffect(() => {
		if (data) {
			const fetchData = async () => {
				setIsLoading(true)
				const respons = await TpsMessagingApi.getTpsPersonInfoAllEnvs(data.ident)
				setBarnInfo(respons.data.contents)
				setIsLoading(false)
			}
			fetchData()
		}
	}, [])

	if (!data) {
		return null
	}

	const erDoedfoedt = data.identtype === 'FDAT'

	return (
		<>
			<div className="person-visning_content">
				<TitleValue title={data.identtype} value={data.ident} visKopier />
				<TitleValue title="Fornavn" value={data.fornavn} />
				<TitleValue title="Mellomnavn" value={data.mellomnavn} />
				<TitleValue title="Etternavn" value={data.etternavn} />
				<TitleValue title="Kjønn" value={formatKjonn(data.kjonn, data.alder)} />
				<TitleValue
					title="Alder"
					value={formatAlderBarn(data.alder, data.doedsdato, erDoedfoedt)}
				/>
				<TitleValue title="Dødsdato" value={formatDate(data.doedsdato)} />
				<TitleValue
					title="Diskresjonskode"
					kodeverk={PersoninformasjonKodeverk.Diskresjonskoder}
					value={data.spesreg}
				/>
				<TitleValue title="Uten fast bopel" value={data.utenFastBopel && 'Ja'} />
				{barnInfo && !isLoading && barnInfo.length > 0 && (
					<TitleValue title="Foreldre" value={finnForeldre(barnInfo[0].relasjoner).join(', ')} />
				)}
				{!erDoedfoedt && (
					<TitleValue
						title="Er adoptert"
						value={data.adoptert ? data.adoptert : oversettBoolean(type === 'BARN')}
					/>
				)}
			</div>
			<Adressevalg data={data} />
		</>
	)
}

const finnForeldre = (relasjoner) => {
	return relasjoner
		.filter((relasjon) => {
			return relasjon.relasjonTypeNavn === 'MOR' || relasjon.relasjonTypeNavn === 'FAR'
		})
		.map((relasjon) => relasjon.personRelasjonMed.ident)
}

import React, { useEffect, useState } from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { TpsfApi } from '~/service/Api'
import { Adressevisning } from './Boadresse'
import { PostadresseVisning } from './Postadresse'
import { Historikk } from '~/components/ui/historikk/Historikk'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

export const Barn = ({ data, type }) => {
	if (!data) return null
	const [barnInfo, setBarnInfo] = useState(null)
	const [isLoading, setIsLoading] = useState(false)

	const erDoedfoedt = data.identtype === 'FDAT'

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
		<>
			<div className="person-visning_content">
				<TitleValue title={data.identtype} value={data.ident} />
				<TitleValue title="Fornavn" value={data.fornavn} />
				<TitleValue title="Mellomnavn" value={data.mellomnavn} />
				<TitleValue title="Etternavn" value={data.etternavn} />
				<TitleValue title="Kjønn" value={Formatters.kjonn(data.kjonn, data.alder)} />
				<TitleValue
					title="Alder"
					value={Formatters.formatAlderBarn(data.alder, data.doedsdato, erDoedfoedt)}
				/>
				<TitleValue title="Dødsdato" value={Formatters.formatDate(data.doedsdato)} />
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
						value={data.adoptert ? data.adoptert : Formatters.oversettBoolean(type === 'BARN')}
					/>
				)}
			</div>
			{data.boadresse.length > 0 && (
				<Historikk component={Adressevisning} propName="boadresse" data={data.boadresse} />
			)}
			{data.postadresse.length > 0 && (
				<Historikk component={PostadresseVisning} propName="postadresse" data={data.postadresse} />
			)}
		</>
	)
}

const finnForeldre = relasjoner => {
	return relasjoner
		.filter(relasjon => {
			return relasjon.relasjonTypeNavn === 'MOR' || relasjon.relasjonTypeNavn === 'FAR'
		})
		.map(relasjon => relasjon.personRelasjonMed.ident)
}

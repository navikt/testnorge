import React, { useEffect, useState } from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { AdresseKodeverk } from '~/config/kodeverk'
import { PersonData } from '~/components/fagsystem/pdlf/PdlTypes'
import { PdlDataVisning } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'
import { DollyApi } from '~/service/Api'

type RelatertPersonData = {
	data: PersonData
	tittel: string
}

export const RelatertPerson = ({ data, tittel }: RelatertPersonData) => {
	if (!data) return null

	const [relatertPersonPdl, setRelatertPersonPdl] = useState(null)

	useEffect(() => {
		getRelatertPersonPdl()
	}, [])

	const getRelatertPersonPdl = async () => {
		await DollyApi.getPersonFraPdl(data.ident).then((response) => {
			setRelatertPersonPdl(response.data)
		})
	}

	return (
		<>
			<div className="person-visning_content">
				<h4 style={{ width: '100%', marginTop: '0' }}>{tittel}</h4>
				<TitleValue title="Ident" value={data.ident} />
				<TitleValue title="Fornavn" value={data.navn?.[0].fornavn} />
				<TitleValue title="Mellomnavn" value={data.navn?.[0].mellomnavn} />
				<TitleValue title="Etternavn" value={data.navn?.[0].etternavn} />
				<TitleValue title="Kjønn" value={data.kjoenn?.[0].kjoenn} />
				<TitleValue
					title="Fødselsdato"
					value={Formatters.formatDate(data.foedsel?.[0].foedselsdato)}
				/>
				<TitleValue
					title="Statsborgerskap"
					value={data.statsborgerskap?.[0].landkode}
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
				/>
				<TitleValue
					title="Gradering"
					value={Formatters.showLabel('gradering', data.adressebeskyttelse?.[0].gradering)}
				/>
			</div>
			{/*<PdlPersonMiljoeInfo data={data} loading={false} />*/}
			<PdlDataVisning pdlData={relatertPersonPdl} />
		</>
	)
}

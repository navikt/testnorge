import React, { useEffect, useState } from 'react'
import styled from 'styled-components'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { Person } from '~/service/services/personsearch/types'
import Formatters from '~/utils/DataFormatter'
import { DollyApi } from '~/service/Api'
import { PdlDataWrapper } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { PdlPersonMiljoeInfo } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlPersonMiljoeinfo'

type PdlResponse = {
	data: PdlDataWrapper
}

type Props = {
	person: Person
}

const Group = styled.div`
	display: flex;
	flex-wrap: wrap;
	margin-bottom: 10px;
`

const Title = styled(TitleValue)`
	margin-bottom: 10px;
`

export const PersonView = ({ person }: Props) => {
	const [data, setData] = useState(null)
	const [loading, setLoading] = useState(true)

	useEffect(() => {
		DollyApi.getPersonFraPdl(person.ident)
			.then((response: PdlResponse) => {
				setData(response?.data?.data)
				setLoading(false)
			})
			.catch((e: any) => {
				setLoading(false)
			})
	}, [])

	return (
		<div>
			<section>
				<SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />
				<Group>
					<Title title="Ident" value={person.ident} />
					<Title title="Aktør Id" value={person.aktorId} />
					<Title title="Fornavn" value={person.fornavn} />
					<Title title="Mellomnavn" value={person.mellomnavn} />
					<Title title="Etternavn" value={person.etternavn} />
					<Title title="Kjønn" value={person.kjoenn} />
					<Title
						title="Fødselsdato"
						value={Formatters.formatStringDates(person.foedsel?.foedselsdato)}
					/>
					<Title title="Sivilstand" value={person.sivilstand?.type} />
					<Title
						title="Personstatus"
						value={person.folkeregisterpersonstatus
							?.map((personstatus) => Formatters.allCapsToCapitalized(personstatus.status))
							.toString()}
					/>
				</Group>
			</section>
			<section>
				<SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />
				<Group>
					<Title
						title="Statsborgerskap"
						value={person.statsborgerskap?.land ? person.statsborgerskap.land.toString() : ''}
					/>
				</Group>
			</section>
			<PdlPersonMiljoeInfo data={data} loading={loading} />
		</div>
	)
}

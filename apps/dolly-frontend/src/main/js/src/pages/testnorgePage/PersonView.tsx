import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import React from 'react'
import styled from 'styled-components'
import { Person } from '~/service/services/personsearch/types'
import Formatters from '~/utils/DataFormatter'

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

export default ({ person }: Props) => (
	<>
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
			</Group>
		</section>
		<section>
			<SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />
			<Group>
				<Title title="Statsborgerskap" value={person.statsborgerskap?.land} />
			</Group>
		</section>
	</>
)
